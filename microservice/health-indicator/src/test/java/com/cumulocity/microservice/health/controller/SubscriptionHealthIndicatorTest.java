package com.cumulocity.microservice.health.controller;

import com.cumulocity.microservice.health.controller.configuration.TestConfiguration;
import com.cumulocity.microservice.health.controller.configuration.TestMicroserviceSubscriptionConfiguration;
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
@SpringBootTest(classes = { TestMicroserviceSubscriptionConfiguration.class, TestConfiguration.class })
public class SubscriptionHealthIndicatorTest {

    @Test
    @WithMockUser(authorities = "ROLE_ACTUATOR")
    public void healthShouldBeDown() {
        when()
                .get("/health").

        then()
                .statusCode(503)
                .contentType(JSON)
                .body("subscription.status", equalTo("DOWN"));
    }
}
