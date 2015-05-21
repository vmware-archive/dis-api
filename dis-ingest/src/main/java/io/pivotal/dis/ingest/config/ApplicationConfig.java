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
import io.pivotal.dis.ingest.service.job.Ingester;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.store.AmazonS3FileStore;
import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;

import java.io.IOException;
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

    public FileStore digestedFileStore() {
        AccessControlList publicReadableAcl = new AccessControlList();
        publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        return new AmazonS3FileStore(amazonS3(), digestedBucketName, publicReadableAcl);
    }

    public FileStore rawFileStore() {
        return new AmazonS3FileStore(amazonS3(), rawBucketName, new AccessControlList());
    }

    private AmazonS3 amazonS3() {
        return new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
    }
}
