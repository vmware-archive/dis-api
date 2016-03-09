package io.pivotal.dis.ingest.app

import io.pivotal.dis.ingest.app.config.ApplicationConfig
import io.pivotal.dis.ingest.app.job.Ingester
import io.pivotal.dis.ingest.app.store.FileStore
import io.pivotal.dis.ingest.app.store.OngoingDisruptionsStore
import io.pivotal.dis.ingest.app.system.Clock
import io.pivotal.dis.ingest.app.system.ClockImpl
import java.net.URL

fun main(args: Array<String>) {
    val applicationConfig = ApplicationConfig()

    startIngesting(
            applicationConfig.tflUrl,
            applicationConfig.rawFileStore,
            applicationConfig.digestedFileStore,
            ClockImpl(),
            OngoingDisruptionsStore())
}

private fun startIngesting(url: URL,
                           rawFileStore: FileStore,
                           digestedFileStore: FileStore,
                           clock: Clock, ongoingDisruptionsStore: OngoingDisruptionsStore) {

    val ingester = Ingester(url, rawFileStore, digestedFileStore, ongoingDisruptionsStore)

    while (true) {
        ingester.ingest(clock)

        try {
            Thread.sleep(60000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
