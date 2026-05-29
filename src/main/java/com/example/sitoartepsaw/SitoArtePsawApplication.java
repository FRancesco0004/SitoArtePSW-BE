package com.example.sitoartepsaw;

import com.example.sitoartepsaw.configurations.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KeycloakProperties.class)
public class SitoArtePsawApplication {

    public static void main(String[] args) {
        SpringApplication.run(SitoArtePsawApplication.class, args);
    }

}
