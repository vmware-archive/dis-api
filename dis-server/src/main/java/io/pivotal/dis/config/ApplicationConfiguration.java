package io.pivotal.dis.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@EnableAutoConfiguration
@ComponentScan("io.pivotal.dis")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {
}
