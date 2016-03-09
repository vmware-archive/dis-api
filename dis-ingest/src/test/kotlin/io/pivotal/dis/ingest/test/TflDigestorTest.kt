package io.pivotal.dis.ingest.test

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.pivotal.dis.ingest.app.job.TflDigestor
import io.pivotal.dis.ingest.domain.Digest
import io.pivotal.dis.ingest.domain.DisruptedLine
import org.apache.commons.io.IOUtils
import org.junit.Test

import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Optional

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat

class TflDigestorTest {

    @Test
    @Throws(Exception::class)
    fun digestTflData_returnsDisruptedLines_WithCorrectDisruptionStartTimes() {
        val currentTime = LocalDateTime.now()

        val earlyTflLineStatus = loadFixture("line_mode_tube_status")
        val earlierDigest = TflDigestor(earlyTflLineStatus,
                currentTime,
                Optional.empty<String>()).digest()

        val digestsAdapter = digestsAdapter

        var digest = digestsAdapter.fromJson(earlierDigest)
        var disruptions = digest.disruptions

        assertThat(disruptions.size, equalTo(1))

        var disruptedLine = disruptions[0]
        assertLineData(disruptedLine, currentTime, "Bakerloo", "Minor Delays", "#FFFFFF", "#AE6118")

        val tenMinutesLater = currentTime.plusMinutes(10)
        val laterTflLineStatus = loadFixture("line_mode_tube_status_2")

        val laterDigest = TflDigestor(laterTflLineStatus,
                tenMinutesLater,
                Optional.of(earlierDigest)).digest()

        digest = digestsAdapter.fromJson(laterDigest)
        disruptions = digest.disruptions

        assertThat(disruptions.size, equalTo(2))

        disruptedLine = disruptions[0]
        assertLineData(disruptedLine, currentTime, "Bakerloo", "Minor Delays", "#FFFFFF", "#AE6118")

        disruptedLine = disruptions[1]
        assertLineData(disruptedLine, tenMinutesLater, "Circle", "Minor Delays", "#113892", "#F8D42D")
    }

    @Test
    @Throws(Exception::class)
    fun digestTflData_predictsEndTimeForEachStatus() {
        val currentTime = LocalDateTime.now()

        val allStatusesJson = loadFixture("endTimeTest")

        val digestJson = TflDigestor(allStatusesJson,
                currentTime,
                Optional.empty<String>()).digest()


        val digestsAdapter = digestsAdapter

        val digest = digestsAdapter.fromJson(digestJson)
        val disruptions = digest.disruptions

        assertThat(disruptions.size, equalTo(6))

        assertThat(disruptions[0].endTime, equalTo(currentTime.plusMinutes(30).format(TIME_FORMAT)))
        assertThat(disruptions[1].endTime, equalTo(currentTime.plusMinutes(60).format(TIME_FORMAT)))
        assertThat(disruptions[2].endTime, equalTo(currentTime.plusDays(1).format(TIME_FORMAT)))
        assertThat(disruptions[3].endTime, equalTo(currentTime.plusDays(1).format(TIME_FORMAT)))
        assertThat(disruptions[4].endTime, equalTo(currentTime.plusMinutes(120).format(TIME_FORMAT)))
        assertThat(disruptions[5].endTime, equalTo(currentTime.format(TIME_FORMAT)))
    }

    @Test
    @Throws(Exception::class)
    fun digestTflData_predictsEarliestPossibleEndTimeForEachStatus() {
        val currentTime = LocalDateTime.now()

        val allStatusesJson = loadFixture("endTimeTest")

        val digestJson = TflDigestor(allStatusesJson,
                currentTime,
                Optional.empty<String>()).digest()

        val digestsAdapter = digestsAdapter

        val digest = digestsAdapter.fromJson(digestJson)
        val disruptions = digest.disruptions

        assertThat(disruptions.size, equalTo(6))

        assertThat(disruptions[0].earliestEndTime, equalTo(currentTime.plusMinutes((30 - 10).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[1].earliestEndTime, equalTo(currentTime.plusMinutes((60 - 20).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[2].earliestEndTime, equalTo(currentTime.plusHours((24 - 8).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[3].earliestEndTime, equalTo(currentTime.plusHours((24 - 8).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[4].earliestEndTime, equalTo(currentTime.plusMinutes((120 - 40).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[5].earliestEndTime, equalTo(currentTime.format(TIME_FORMAT)))
    }

    @Test
    @Throws(Exception::class)
    fun digestTflData_predictsLatestPossibleEndTimeForEachStatus() {
        val currentTime = LocalDateTime.now()

        val allStatusesJson = loadFixture("endTimeTest")

        val digestJson = TflDigestor(allStatusesJson,
                currentTime,
                Optional.empty<String>()).digest()

        val digestsAdapter = digestsAdapter

        val digest = digestsAdapter.fromJson(digestJson)
        val disruptions = digest.disruptions

        assertThat(disruptions.size, equalTo(6))

        assertThat(disruptions[0].latestEndTime, equalTo(currentTime.plusMinutes((30 + 10).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[1].latestEndTime, equalTo(currentTime.plusMinutes((60 + 20).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[2].latestEndTime, equalTo(currentTime.plusHours((24 + 8).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[3].latestEndTime, equalTo(currentTime.plusHours((24 + 8).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[4].latestEndTime, equalTo(currentTime.plusMinutes((120 + 40).toLong()).format(TIME_FORMAT)))
        assertThat(disruptions[5].latestEndTime, equalTo(currentTime.format(TIME_FORMAT)))
    }

    @Throws(IOException::class)
    private fun loadFixture(name: String): String {
        return IOUtils.toString(javaClass.classLoader.getResourceAsStream(name + ".json"))
    }

    private val digestsAdapter: JsonAdapter<Digest>
        get() {
            val moshi = Moshi.Builder().build()
            return moshi.adapter(Digest::class.java)
        }

    private fun assertLineData(disruptedLine: DisruptedLine,
                               currentTime: LocalDateTime,
                               expectedLine: String,
                               expectedStatus: String,
                               expectedForegroundColor: String,
                               expectedBackgroundColor: String) {

        assertThat(disruptedLine.line, equalTo(expectedLine))

        assertThat(disruptedLine.foregroundColor, equalTo(expectedForegroundColor))
        assertThat(disruptedLine.backgroundColor, equalTo(expectedBackgroundColor))

        assertThat(disruptedLine.startTime, equalTo(currentTime.format(TIME_FORMAT)))
        assertThat(disruptedLine.endTime, equalTo(currentTime.plusMinutes(30).format(TIME_FORMAT)))
        assertThat(disruptedLine.earliestEndTime, equalTo(currentTime.plusMinutes((30 - 10).toLong()).format(TIME_FORMAT)))
        assertThat(disruptedLine.latestEndTime, equalTo(currentTime.plusMinutes((30 + 10).toLong()).format(TIME_FORMAT)))

        assertThat(epochMillisToTimeString(disruptedLine.startTimestamp), equalTo(currentTime.format(TIME_FORMAT)))
        assertThat(epochMillisToTimeString(disruptedLine.endTimestamp), equalTo(currentTime.plusMinutes(30).format(TIME_FORMAT)))
        assertThat(epochMillisToTimeString(disruptedLine.earliestEndTimestamp), equalTo(currentTime.plusMinutes((30 - 10).toLong()).format(TIME_FORMAT)))
        assertThat(epochMillisToTimeString(disruptedLine.latestEndTimestamp), equalTo(currentTime.plusMinutes((30 + 10).toLong()).format(TIME_FORMAT)))

        assertThat(disruptedLine.status, equalTo(expectedStatus))
    }

    private fun epochMillisToTimeString(millis: Long?): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis!!), ZoneOffset.UTC).format(TIME_FORMAT)
    }

    companion object {

        private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
    }

}
