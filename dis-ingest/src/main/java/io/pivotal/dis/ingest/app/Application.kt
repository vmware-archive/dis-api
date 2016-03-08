package io.pivotal.dis.ingest.app

import com.amazonaws.services.s3.model.Bucket
import io.pivotal.dis.ingest.config.ApplicationConfig
import io.pivotal.dis.ingest.job.Ingester
import io.pivotal.dis.ingest.store.FileStore
import io.pivotal.dis.ingest.store.OngoingDisruptionsStore
import io.pivotal.dis.ingest.system.Clock
import io.pivotal.dis.ingest.system.ClockImpl
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException

import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

object Application {
    @Throws(IOException::class, CloudFoundryEnvironmentException::class, URISyntaxException::class)
    @JvmStatic fun main(args: Array<String>) {
        val applicationConfig = ApplicationConfig()

        startIngesting(
                applicationConfig.tflUrl(),
                applicationConfig.rawFileStore(),
                applicationConfig.digestedFileStore(),
                ClockImpl(),
                OngoingDisruptionsStore())
    }

    private fun startIngesting(url: URL,
                               rawFileStore: FileStore,
                               digestedFileStore: FileStore,
                               clock: Clock, ongoingDisruptionsStore: OngoingDisruptionsStore) {

        val ingester = Ingester(url,
                rawFileStore,
                digestedFileStore,
                ongoingDisruptionsStore)

        while (true) {
            ingester.ingest(clock)

            try {
                Thread.sleep(60000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    private fun findBucket(buckets: List<Bucket>, name: String): Bucket {
        return buckets.filter({ b -> b.name == name }).first()
    }

}
