package io.pivotal.dis.ingest.config;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@ComponentScan("io.pivotal.dis.ingest")
@EnableScheduling
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
    @Bean
    public AmazonS3 amazonS3() {
        return new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
