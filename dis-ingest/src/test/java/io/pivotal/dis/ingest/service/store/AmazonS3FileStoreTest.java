package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

public class AmazonS3FileStoreTest {

    @Test
    public void savesTimestampedFileToS3() {
        Map<String, Object> params = new HashMap<>();
        AmazonS3 mockAmazonS3 = proxy(AmazonS3.class, "putObject", args -> {
            PutObjectRequest putObjectRequest = (PutObjectRequest) args[0];
            params.put("bucketName", putObjectRequest.getBucketName());
            params.put("key", putObjectRequest.getKey());
            params.put("input", toString((InputStream) putObjectRequest.getInputStream()));
            params.put("aclGrant", putObjectRequest.getAccessControlList().getGrants());
        });

        AmazonS3FileStore fileStore = new AmazonS3FileStore(mockAmazonS3, "bucketName");
        fileStore.save("supplied filename", "some stuff");

        assertThat(params, allOf(
                hasEntry("input", "some stuff"),
                hasEntry("bucketName", "bucketName"),
                hasEntry("key", "supplied filename"),
                hasKey("aclGrant")
        ));
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
