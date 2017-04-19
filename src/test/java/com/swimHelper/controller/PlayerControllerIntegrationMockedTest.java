package com.swimHelper.controller;

import com.swimHelper.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pllstrobin on 4/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ComponentScan
public class PlayerControllerIntegrationMockedTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private UserRepository playerRepositoryMock;

    @Test
    public void ensureThatAllPlayersAreReturnedFromEndpoint() {
        Mockito.when(playerRepositoryMock.findAll()).thenThrow(IllegalStateException.class);

        ResponseEntity<String> allPlayersResponse = testRestTemplate.getForEntity("/users/", String.class);
        assertThat(allPlayersResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
