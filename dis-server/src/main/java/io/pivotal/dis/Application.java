package io.pivotal.dis;

import io.pivotal.dis.config.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }
}
