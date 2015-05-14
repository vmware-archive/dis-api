package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AmazonS3FileStoreTest {

    @Test
    public void savesTimestampedFileToS3WithPubliclyReadableAcl() {
        Map<String, Object> params = new HashMap<>();
        AmazonS3 mockAmazonS3 = proxy(AmazonS3.class, "putObject", args -> {
            PutObjectRequest putObjectRequest = (PutObjectRequest) args[0];
            params.put("bucketName", putObjectRequest.getBucketName());
            params.put("key", putObjectRequest.getKey());
            params.put("input", toString(putObjectRequest.getInputStream()));
            params.put("acl", putObjectRequest.getAccessControlList());
        });
        AccessControlList publicReadableAcl = new AccessControlList();
        publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        AmazonS3FileStore fileStore = new AmazonS3FileStore(mockAmazonS3, "bucketName", publicReadableAcl);
        fileStore.save("supplied filename", "some stuff");

        assertThat(params, allOf(
                hasEntry("input", "some stuff"),
                hasEntry("bucketName", "bucketName"),
                hasEntry("key", "supplied filename"),
                hasKey("acl")
        ));

        AccessControlList acl = (AccessControlList) params.get("acl");
        assertThat(acl, equalTo(publicReadableAcl));
    }

    @Test
    public void savesTimestampedFileToS3WithDefaultAcl() {
        Map<String, Object> params = new HashMap<>();
        AmazonS3 mockAmazonS3 = proxy(AmazonS3.class, "putObject", args -> {
            PutObjectRequest putObjectRequest = (PutObjectRequest) args[0];
            params.put("bucketName", putObjectRequest.getBucketName());
            params.put("key", putObjectRequest.getKey());
            params.put("input", toString(putObjectRequest.getInputStream()));
            params.put("acl", putObjectRequest.getAccessControlList());
        });
        AccessControlList defaultAcl = new AccessControlList();
        AmazonS3FileStore fileStore = new AmazonS3FileStore(mockAmazonS3, "bucketName", defaultAcl);
        fileStore.save("supplied filename", "some stuff");

        assertThat(params, allOf(
                hasEntry("input", "some stuff"),
                hasEntry("bucketName", "bucketName"),
                hasEntry("key", "supplied filename"),
                hasKey("acl")
        ));


        AccessControlList acl = (AccessControlList) params.get("acl");
        assertThat(acl, equalTo(defaultAcl));
    }

    private String toString(InputStream stream) {
        try {
            return IOUtils.toString(stream);
        } catch (IOException e) {
            return e.toString();
        }
    }

    private <T> T proxy(Class<T> interfaceClass, String methodName, Consumer<Object[]> handler) {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            if (method.getName().equals(methodName)) {
                handler.accept(args);
            }
            return null;
        };
        return interfaceClass.cast(Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler));
    }

}
