package io.pivotal.dis.ingest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@Profile("local")
public class LocalApplicationConfig extends ApplicationConfig {

    @Value("${tfl.url}")
    private String tflUrlString;

    @PostConstruct
    public void initialize() throws MalformedURLException {
        tflUrl = new URL(tflUrlString);
    }
}
