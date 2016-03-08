package io.pivotal.dis.ingest.job

import com.amazonaws.util.json.JSONException
import io.pivotal.dis.ingest.store.FileStore
import io.pivotal.dis.ingest.store.OngoingDisruptionsStore
import io.pivotal.dis.ingest.system.Clock
import org.apache.commons.io.IOUtils

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Optional

class Ingester(private val url: URL,
               private val fileStore: FileStore,
               private val digestedFileStore: FileStore,
               private val ongoingDisruptionsStore: OngoingDisruptionsStore) {

    fun ingest(clock: Clock) {
        url.openConnection().inputStream.use { inputStream ->
            val tflData = IOUtils.toString(inputStream)
            fileStore.save(nameRawFile(clock), tflData)

            val previousDisruptionDigest = Optional.ofNullable<String>(ongoingDisruptionsStore.previousDisruptionDigest)

            val digestedTflData = TflDigestor(
                    tflData,
                    clock.currentTime,
                    previousDisruptionDigest).digest()

            digestedFileStore.save("disruptions.json", digestedTflData)
            ongoingDisruptionsStore.previousDisruptionDigest = digestedTflData
        }
    }

    private fun nameRawFile(clock: Clock): String {
        val timestamp = clock.currentTime.atOffset(ZoneOffset.UTC).format(FILE_NAME_DATE_TIME)

        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp)
    }

    companion object {
        private val FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
    }

}
