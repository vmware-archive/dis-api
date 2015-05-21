package io.pivotal.dis.ingest.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import io.pivotal.dis.ingest.service.job.Clock;
import io.pivotal.dis.ingest.service.job.ClockImpl;
import io.pivotal.dis.ingest.service.job.Ingester;
import io.pivotal.dis.ingest.service.store.AmazonS3FileStore;
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
                new OngoingDisruptionsStore(),
                new ClockImpl()
        );
    }

    private static void startIngesting(URL url, FileStore rawFileStore, FileStore digestedFileStore, OngoingDisruptionsStore ongoingDisruptionsStore, Clock clock) {
        Ingester ingester = new Ingester(url, rawFileStore, digestedFileStore, clock, ongoingDisruptionsStore);

        while (true) {
            ingester.ingest();

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
