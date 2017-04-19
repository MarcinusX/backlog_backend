package com.swimHelper.controller;

import com.jayway.restassured.path.json.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pllstrobin on 4/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerFullIntegrationTest {

    @Value("${test.db.initializer.users.size}")
    private Integer playerSize;

    @Autowired
    private TestRestTemplate testRestTemplate;

//    @MockBean
//    private PlayerRepository playerRepositoryMock;

    @Test
    public void ensureThatAllPlayersAreReturnedFromEndpoint() {

        ResponseEntity<String> allUserResponse = testRestTemplate.getForEntity("/users", String.class);
        assertThat(allUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonPath jsonPath = new JsonPath(allUserResponse.getBody());
        List<String> emails = jsonPath.get("email");
        assertThat(emails).isNotNull().hasSize(playerSize);
    }

}
