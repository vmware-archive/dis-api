package io.pivotal.dis.ingest.test

import com.amazonaws.util.json.JSONException
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import io.pivotal.dis.ingest.domain.Digest
import io.pivotal.dis.ingest.domain.DisruptedLine
import io.pivotal.dis.ingest.domain.tfl.TflLine
import io.pivotal.dis.ingest.domain.tfl.LineStatus
import io.pivotal.dis.ingest.app.store.FileStore
import io.pivotal.dis.ingest.app.store.OngoingDisruptionsStore
import io.pivotal.dis.ingest.app.system.Clock
import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squareup.moshi.Types.newParameterizedType
import io.pivotal.dis.ingest.app.job.Ingester
import java.util.Arrays.asList
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Assert.assertThat
import java.util.*

class IngestionTest {

    private lateinit var tflMockWebServer: MockWebServer
    private lateinit var rawFileStore: MockFileStore
    private lateinit var digestedFileStore: MockFileStore
    private lateinit var ongoingDisruptionsStore: OngoingDisruptionsStore

    @Before
    @Throws(Exception::class)
    fun prepareServer() {
        tflMockWebServer = MockWebServer()
        rawFileStore = MockFileStore()
        digestedFileStore = MockFileStore()
        ongoingDisruptionsStore = OngoingDisruptionsStore()

        val moshiTflLinesAdapter = moshiTflLinesAdapter()

        var tflTflLines: MutableList<TflLine> = ArrayList()
        tflTflLines.add(stubLine("Bakerloo", "Runaway Train"))

        tflMockWebServer.enqueue(
                MockResponse().setHeader("Content-Type", "application/json").setBody(moshiTflLinesAdapter.toJson(tflTflLines)))

        tflTflLines = ArrayList<TflLine>()
        tflTflLines.add(stubLine("Bakerloo", "Runaway Train"))
        tflTflLines.add(stubLine("Circle", "Leaves on the TflLine"))

        tflMockWebServer.enqueue(
                MockResponse().setHeader("Content-Type", "application/json").setBody(moshiTflLinesAdapter.toJson(tflTflLines)))

        tflMockWebServer.play()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun savesTflDataToFileStore() {
        val clock = FakeClock(LocalDateTime.now())

        val job = createIngester()
        job.ingest(clock)

        assertLastRawFileCreated(clock)

        val tflLines = lastRawFileContent

        assertThat(tflLines[0], equalTo(stubLine("Bakerloo", "Runaway Train")))
    }

    @Test
    @Throws(IOException::class)
    fun savesTflDataToFileStoreForTwoSuccessiveIngestJobs() {
        val clock = FakeClock(LocalDateTime.now())

        val job = createIngester()
        job.ingest(clock)

        assertLastRawFileCreated(clock)

        var tflLines = lastRawFileContent

        assertThat(tflLines[0], equalTo(stubLine("Bakerloo", "Runaway Train")))


        clock.currentTime = LocalDateTime.now().plusMinutes(10)
        job.ingest(clock)

        assertLastRawFileCreated(clock)

        tflLines = lastRawFileContent

        assertThat(tflLines[0], equalTo(stubLine("Bakerloo", "Runaway Train")))
        assertThat(tflLines[1], equalTo(stubLine("Circle", "Leaves on the TflLine")))
    }

    @Test
    @Throws(Exception::class)
    fun savesTranslatedDataToFileStore() {
        val clock = FakeClock(LocalDateTime.now())

        val job = createIngester()
        job.ingest(clock)

        assertDigestedFileCreated()

        val digest = lastDigest

        val disruptions = digest.disruptions

        assertThat(disruptions, hasSize<DisruptedLine>(1))

        val currentTime = LocalDateTime.now()
        assertLineData(disruptions[0], "Bakerloo", "Runaway Train", currentTime, currentTime, currentTime, currentTime)
    }

    @Test
    @Throws(Exception::class)
    fun savesTranslatedDataToFileStoreForTwoSuccessiveIngestJobs() {
        val currentTime = LocalDateTime.now()

        val clock = FakeClock(currentTime)

        val job = createIngester()
        job.ingest(clock)

        clock.currentTime = currentTime.plusMinutes(10)
        job.ingest(clock)

        assertDigestedFileCreated()

        val digest = lastDigest
        val disruptions = digest.disruptions

        assertThat(disruptions, hasSize<DisruptedLine>(2))

        assertLineData(disruptions[0], "Bakerloo", "Runaway Train", currentTime, currentTime, currentTime, currentTime)
        assertLineData(disruptions[1], "Circle", "Leaves on the TflLine", currentTime.plusMinutes(10), currentTime.plusMinutes(10), currentTime.plusMinutes(10), currentTime.plusMinutes(10))
    }


    private fun stubLine(name: String, vararg lineStatus: String): TflLine {
        val tflLine = TflLine(name, stubLineStatuses(*lineStatus))
        return tflLine
    }

    private fun stubLineStatuses(vararg lineStatus: String): List<LineStatus> {
        return asList(*lineStatus).map({ l -> LineStatus(l) })
    }

    private fun assertLineData(disruptedLine: DisruptedLine,
                               expectedLine: String,
                               expectedStatus: String,
                               expectedStartTime: LocalDateTime,
                               expectedEndTime: LocalDateTime,
                               expectedEarliestEndTime: LocalDateTime,
                               expectedLatestEndTime: LocalDateTime) {

        assertThat(disruptedLine.line, equalTo(expectedLine))
        assertThat(disruptedLine.startTime, equalTo(expectedStartTime.format(TIME_FORMAT)))
        assertThat(disruptedLine.endTime, equalTo(expectedEndTime.format(TIME_FORMAT)))
        assertThat(disruptedLine.earliestEndTime, equalTo(expectedEarliestEndTime.format(TIME_FORMAT)))
        assertThat(disruptedLine.latestEndTime, equalTo(expectedLatestEndTime.format(TIME_FORMAT)))
        assertThat(disruptedLine.status, equalTo(expectedStatus))
    }

    private fun createIngester(): Ingester {
        return Ingester(
                tflMockWebServer.getUrl("/"),
                rawFileStore,
                digestedFileStore)
    }

    private fun moshiTflLinesAdapter(): JsonAdapter<List<TflLine>> {
        return moshi().adapter<List<TflLine>>(newParameterizedType(List::class.java, TflLine::class.java))
    }

    private fun moshiDigestAdapter(): JsonAdapter<Digest> {
        return moshi().adapter(Digest::class.java)
    }

    private fun moshi(): Moshi {
        return Moshi.Builder().build()
    }

    private fun assertLastRawFileCreated(clock: Clock) {
        assertThat<String>(rawFileStore.lastName, equalTo("tfl_api_line_mode_status_tube_" + clock.currentTime.format(DATE_TIME_FORMAT) + ".json"))
    }

    private val lastRawFileContent: List<TflLine>
        @Throws(IOException::class)
        get() {
            val lastFileAsJson = rawFileStore.lastFile
            return moshiTflLinesAdapter().fromJson(lastFileAsJson)
        }

    private fun assertDigestedFileCreated() {
        assertThat<String>(digestedFileStore.lastName, equalTo("disruptions.json"))
    }

    private val lastDigest: Digest
        @Throws(IOException::class)
        get() {
            val lastFileAsJson = digestedFileStore.lastFile
            return moshiDigestAdapter().fromJson(lastFileAsJson)
        }

    private inner class MockFileStore : FileStore {

        private val fileMap = HashMap<String, String>()

        override fun read(name: String): String? {
            return fileMap[name]
        }

        var lastName: String? = null
            private set
        var lastFile: String? = null
            private set

        override fun save(name: String, input: String) {
            lastName = name
            lastFile = input

            fileMap.put(name, input)
        }

    }

    private class FakeClock(private var _time: LocalDateTime) : Clock {

        override var currentTime: LocalDateTime
            get() = _time
            set(value) {
                _time = value
            }

    }

    companion object {
        private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
        private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
    }
}
