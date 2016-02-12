package io.pivotal.dis.ingest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@Profile("cloud")
public class CloudApplicationConfig extends ApplicationConfig{

    @Value("${vcap.services.tfl.credentials.uri}")
    private String tflUrlString;

    @PostConstruct
    public void initialize() throws MalformedURLException {
        tflUrl = new URL(tflUrlString);
    }
}
