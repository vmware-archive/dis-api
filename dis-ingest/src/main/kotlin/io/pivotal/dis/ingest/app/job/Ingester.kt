package io.pivotal.dis.ingest.app.job

import io.pivotal.dis.ingest.app.store.FileStore
import io.pivotal.dis.ingest.app.store.OngoingDisruptionsStore
import io.pivotal.dis.ingest.app.system.Clock
import org.apache.commons.io.IOUtils
import java.net.URL
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class Ingester(private val url: URL,
               private val fileStore: FileStore,
               private val digestedFileStore: FileStore) {

    fun ingest(clock: Clock) {
        url.openConnection().inputStream.use { inputStream ->
            val tflData = IOUtils.toString(inputStream)
            fileStore.save(nameRawFile(clock), tflData)

            val previousDisruptionDigest = digestedFileStore.read("disruptions.json")
            val previousDisruptionDigestOptional = if (previousDisruptionDigest != null) {
                Optional.of(previousDisruptionDigest)
            } else {
                Optional.empty<String>()
            }

            val digestedTflData = Digestor(
                    tflData,
                    clock.currentTime,
                    previousDisruptionDigestOptional).digest()

            digestedFileStore.save("disruptions.json", digestedTflData)
        }
    }

    private fun nameRawFile(clock: Clock): String {
        val timestamp = clock.currentTime.atOffset(ZoneOffset.UTC).format(Ingester.Companion.FILE_NAME_DATE_TIME)

        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp)
    }

    companion object {
        private val FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
    }

}
