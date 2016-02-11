package io.pivotal.dis.ingest.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import io.pivotal.dis.ingest.store.AmazonS3FileStore;
import io.pivotal.dis.ingest.store.FileStore;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;

public abstract class ApplicationConfig {

    @Value("${s3.bucket.name.raw}")
    private String rawBucketName;

    @Value("${s3.bucket.name.digested}")
    private String digestedBucketName;

    protected URL tflUrl;

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
