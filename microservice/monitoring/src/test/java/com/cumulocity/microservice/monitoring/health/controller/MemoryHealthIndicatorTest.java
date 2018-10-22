package com.cumulocity.microservice.monitoring.health.controller;

import com.cumulocity.microservice.monitoring.health.controller.configuration.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest(classes = TestConfiguration.class)
public class MemoryHealthIndicatorTest {

    @Test
    @WithMockUser(authorities = "ROLE_ACTUATOR")
    public void healthShouldBeUp() {
        when()
                .get("/health").

        then()
                .statusCode(200)
                .contentType(JSON)
                .body("nonHeapMemory.status", equalTo("UP"))
                .body("heapMemory.status", equalTo("UP"))
                .body("diskSpace.status", equalTo("UP"));
    }
}