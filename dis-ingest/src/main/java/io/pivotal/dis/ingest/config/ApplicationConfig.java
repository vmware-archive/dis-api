package io.pivotal.dis.ingest.config;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import io.pivotal.dis.ingest.service.job.EveryMinuteFixedRunner;
import io.pivotal.dis.ingest.service.job.IngestJob;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.store.FileStoreImpl;
import io.pivotal.dis.ingest.service.tfl.UrlProvider;
import io.pivotal.dis.ingest.service.tfl.UrlProviderImpl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Properties;

public class ApplicationConfig {

    private Properties properties = new Properties();
    private String propertyFileName = "application.properties";
    private URL tflUrl;
    private String bucketName;

    public ApplicationConfig() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propertyFileName + "' not found in the classpath");
            }
            tflUrl = new URL(properties.getProperty("tfl.url"));
            bucketName = properties.getProperty("s3.bucketName");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public URL tflUrl(){
        return tflUrl;
    }

    public String bucketName(){
        return bucketName;
    }

    public AmazonS3 amazonS3() {
        return new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }

    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        UrlProvider urlProvider = new UrlProviderImpl(applicationConfig.tflUrl());
        FileStore fileStore = new FileStoreImpl(applicationConfig.amazonS3(), LocalDateTime::now, applicationConfig.bucketName);

        // Jobs
        EveryMinuteFixedRunner runner = new EveryMinuteFixedRunner();
        runner.addRunnable(new IngestJob(urlProvider, fileStore));

    }
}
