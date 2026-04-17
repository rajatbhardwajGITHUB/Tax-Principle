package com.example.Usermangement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.WebApplicationType;

class UsermangementApplicationTests {

    @Test
    void contextLoads() {
        try (var context = new SpringApplicationBuilder(UsermangementApplication.class)
                .profiles("test")
                .web(WebApplicationType.NONE)
                .run()) {
            // The application context should start and shut down cleanly.
        }
    }
}
