package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
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
import static org.junit.Assert.assertThat;

public class FileStoreImplTest {

    @Test
    public void savesTimestampedFileToS3() {
        Map<String, Object> params = new HashMap<>(); // blech
        AmazonS3 mockAmazonS3 = proxy(AmazonS3.class, "putObject", args -> {
            params.put("bucketName", args[0]);
            params.put("key", args[1]);
            params.put("input", toString((InputStream) args[2]));
        });

        FileStoreImpl fileStore = new FileStoreImpl(mockAmazonS3, "bucketName");
        fileStore.save("supplied filename", "some stuff");

        assertThat(params, allOf(
                hasEntry("input", "some stuff"),
                hasEntry("bucketName", "bucketName"),
                hasEntry("key", "supplied filename")
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
