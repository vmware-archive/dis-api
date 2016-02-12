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
import org.springframework.context.annotation.Bean;

import java.net.URL;

public abstract class ApplicationConfig {

    protected URL tflUrl;

    @Bean
    public URL tflUrl() {
        return tflUrl;
    }

    @Bean
    public FileStore rawFileStore(
            @Value("${s3.bucket.name.raw}") String rawBucketName) {

        return new AmazonS3FileStore(amazonS3(), rawBucketName, new AccessControlList());
    }

    @Bean
    public FileStore digestedFileStore(
            @Value("${s3.bucket.name.digested}") String digestedBucketName) {

        AccessControlList publicReadableAcl = new AccessControlList();
        publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        return new AmazonS3FileStore(amazonS3(), digestedBucketName, publicReadableAcl);
    }

    private AmazonS3 amazonS3() {
        return new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
    }

}
