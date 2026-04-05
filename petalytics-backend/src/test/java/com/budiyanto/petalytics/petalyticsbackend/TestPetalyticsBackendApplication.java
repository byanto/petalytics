package com.budiyanto.petalytics.petalyticsbackend;

import org.springframework.boot.SpringApplication;

public class TestPetalyticsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(PetalyticsBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
