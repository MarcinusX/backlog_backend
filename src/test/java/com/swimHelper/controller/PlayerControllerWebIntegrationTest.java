package com.swimHelper.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by lukasz on 3/28/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PlayerControllerWebIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testListAll() throws IOException {

        ResponseEntity<String> response = restTemplate.getForEntity("/users", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode responseJson = mapper.readTree(response.getBody());

        System.out.println();
    }


}
