package com.swimHelper.controller.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.repository.CompetitionRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class CompetitionEndToEndTest {

    @Autowired
    TestUtil testUtil;
    @Autowired
    CompetitionTestUtil competitionTestUtil;
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    JsonUtil jsonUtil;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;

    @Before
    public void prepare() {
        cleanUp();
    }

    @After
    public void cleanUp() {
        competitionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void competitionEndToEndTest() throws Exception {
        //create 3 users
        User user1 = testUtil.createValidUser();
        User user2 = testUtil.createValidUser();
        user2.setEmail("new@email.com");
        ResponseEntity<User> user1Entity = testUtil.postUser(testRestTemplate, user1);
        ResponseEntity<User> user2Entity = testUtil.postUser(testRestTemplate, user2);

        //create competition
        Competition competition = competitionTestUtil.createValidCompetition();
        competition.setMaxParticipants(1);
        ResponseEntity<Competition> competitionResponseEntity =
                testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                        .postForEntity("/competitions", competition, Competition.class);
        long competitionId = competitionResponseEntity.getBody().getId();

        //assignToCompetition
        ResponseEntity<Competition> competitionResponse =
                testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                        .postForEntity("/competitions/" + competitionId, null, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        competition = competitionResponse.getBody();
        assertThat(competition.getParticipantsCounter()).isEqualTo(1);

        //try second assign
        competitionResponse =
                testRestTemplate.withBasicAuth("new@email.com", "somePassword")
                        .postForEntity("/competitions/" + competitionId, null, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //update request
        competition.setMaxParticipants(2);
        competitionResponse = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions",
                        HttpMethod.PUT,
                        createCompetitionEntity(competition),
                        Competition.class);

        competition = competitionResponse.getBody();
        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(competition.getMaxParticipants()).isEqualTo(2);

        //second assign
        competitionResponse =
                testRestTemplate.withBasicAuth("new@email.com", "somePassword")
                        .postForEntity("/competitions/" + competitionId, null, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(competitionResponse.getBody().getParticipantsCounter()).isEqualTo(2);
    }

    private HttpEntity createCompetitionEntity(Competition competition) {
        String json = jsonUtil.toJson(competition);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }
}
