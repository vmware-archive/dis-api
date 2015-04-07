package io.pivotal.dis.ingest.config;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import io.pivotal.dis.ingest.service.job.EveryMinuteFixedRunner;
import io.pivotal.dis.ingest.service.job.IngestJob;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.store.FileStoreImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class ApplicationConfig {

    private URL tflUrl;
    private String bucketName;

    public ApplicationConfig() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = openResource("application.properties")) {
            properties.load(inputStream);
        }

        tflUrl = new URL(properties.getProperty("tfl.url"));
        bucketName = properties.getProperty("s3.bucketName");
    }

    private InputStream openResource(String name) throws FileNotFoundException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new FileNotFoundException("file '" + name + "' not found in the classpath");
        }
        return inputStream;
    }

    public URL tflUrl() {
        return tflUrl;
    }

    public String bucketName() {
        return bucketName;
    }

    public AmazonS3 amazonS3() {
        return new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }

    public static void main(String[] args) throws IOException {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        URL url = applicationConfig.tflUrl();
        FileStore fileStore = new FileStoreImpl(applicationConfig.amazonS3(), applicationConfig.bucketName());

        // Jobs
        EveryMinuteFixedRunner runner = new EveryMinuteFixedRunner();
        runner.addRunnable(new IngestJob(url, fileStore));
    }

}
