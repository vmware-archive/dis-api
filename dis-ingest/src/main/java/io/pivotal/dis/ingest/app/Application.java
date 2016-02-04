package io.pivotal.dis.ingest.app;

import com.amazonaws.services.s3.model.Bucket;
import io.pivotal.dis.ingest.config.ApplicationConfig;
import io.pivotal.dis.ingest.service.job.Clock;
import io.pivotal.dis.ingest.service.job.ClockImpl;
import io.pivotal.dis.ingest.service.job.Ingester;
import io.pivotal.dis.ingest.service.job.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Application {
    public static void main(String[] args) throws IOException, CloudFoundryEnvironmentException, URISyntaxException {
        ApplicationConfig applicationConfig = new ApplicationConfig();

//        System.out.println("Raw bucket: " + findBucket(buckets, applicationConfig.rawBucketName()));
//        System.out.println("Digested bucket: " + findBucket(buckets, applicationConfig.digestedBucketName()));

        startIngesting(
                applicationConfig.tflUrl(),
                applicationConfig.rawFileStore(),
                applicationConfig.digestedFileStore(),
                new ClockImpl(),
                new OngoingDisruptionsStore()
        );
    }

    private static void startIngesting(URL url,
                                       FileStore rawFileStore,
                                       FileStore digestedFileStore,
                                       Clock clock, OngoingDisruptionsStore ongoingDisruptionsStore) {

        Ingester ingester =
                new Ingester(url,
                            rawFileStore,
                            digestedFileStore,
                        ongoingDisruptionsStore);

        while (true) {
            ingester.ingest(clock);

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bucket findBucket(List<Bucket> buckets, String name) {
        return buckets.stream().filter(b -> b.getName().equals(name)).findFirst().get();
    }

}
