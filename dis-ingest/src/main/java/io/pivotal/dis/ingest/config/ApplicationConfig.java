package io.pivotal.dis.ingest.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;

import io.pivotal.dis.ingest.service.job.Clock;
import io.pivotal.dis.ingest.service.job.ClockImpl;
import io.pivotal.dis.ingest.service.job.IngestJob;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.store.AmazonS3FileStore;
import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class ApplicationConfig {

    private final URL tflUrl;
    private final String rawBucketName;
    private final String digestedBucketName;

    public ApplicationConfig() throws IOException, CloudFoundryEnvironmentException, URISyntaxException {
        CloudFoundryEnvironment cloudFoundryEnvironment = new CloudFoundryEnvironment(System::getenv);
        tflUrl = cloudFoundryEnvironment.getService("tfl").getUri().toURL();
        rawBucketName = System.getenv("S3_BUCKET_NAME_RAW");
        digestedBucketName = System.getenv("S3_BUCKET_NAME_DIGESTED");
    }

    public URL tflUrl() {
        return tflUrl;
    }

    public String rawBucketName() {
        return rawBucketName;
    }

    public String digestedBucketName() {
        return digestedBucketName;
    }

    public AmazonS3 amazonS3() {
        return new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
    }

    public static void main(String[] args) throws IOException, CloudFoundryEnvironmentException, URISyntaxException {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        URL url = applicationConfig.tflUrl();

        AmazonS3 amazonS3 = applicationConfig.amazonS3();
        List<Bucket> buckets = amazonS3.listBuckets();
        System.out.println("Raw bucket: " + findBucket(buckets, applicationConfig.rawBucketName()));
        System.out.println("Digested bucket: " + findBucket(buckets, applicationConfig.digestedBucketName()));

        FileStore rawFileStore = new AmazonS3FileStore(amazonS3, applicationConfig.rawBucketName(), new AccessControlList());
        AccessControlList publicReadableAcl = new AccessControlList();
        publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        FileStore digestedFileStore = new AmazonS3FileStore(amazonS3, applicationConfig.digestedBucketName(), publicReadableAcl);
        OngoingDisruptionsStore ongoingDisruptionsStore = new OngoingDisruptionsStore();

        Clock clock = new ClockImpl();
        IngestJob ingestJob = new IngestJob(url, rawFileStore, digestedFileStore, clock, ongoingDisruptionsStore);

        while (true) {
            ingestJob.run();

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
