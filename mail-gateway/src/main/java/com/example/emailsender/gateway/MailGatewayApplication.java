package com.example.emailsender.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailGatewayApplication.class, args);
    }
}
