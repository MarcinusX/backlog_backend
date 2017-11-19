package com.swimHelper.controller.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.security.JwtUser;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
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

import static com.swimHelper.security.SecurityConstants.HEADER_STRING;
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
    TrainingTestUtil trainingTestUtil;
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
        JwtUser jwtUser1 = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader1 = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser1);
        //create competition
        Competition competition1 = competitionTestUtil.createValidCompetition();
        competition1.setMaxParticipants(1);
        String json = jsonUtil.toJson(competition1);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.set(HEADER_STRING, authorizationHeader1);
        HttpEntity<String> entity1 = new HttpEntity<>(json, headers1);
        ResponseEntity<Competition> competitionResponseEntity =
                testRestTemplate.postForEntity("/competitions", entity1, Competition.class);
        long competitionId = competitionResponseEntity.getBody().getId();

        //assignToCompetition
        HttpEntity<String> entity2 = new HttpEntity<>(headers1);
        ResponseEntity<Competition> competitionResponse =
                testRestTemplate.postForEntity("/competitions/" + competitionId, entity2, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        competition1 = competitionResponse.getBody();
        assertThat(competition1.getParticipantsCounter()).isEqualTo(1);

        //try second assign
        JwtUser user3 = new JwtUser("new@email.com", TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader3 = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user3);
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setContentType(MediaType.APPLICATION_JSON);
        headers3.set(HEADER_STRING, authorizationHeader3);
        HttpEntity<String> entity3 = new HttpEntity<>(headers3);
        competitionResponse =
                testRestTemplate.postForEntity("/competitions/" + competitionId, entity3, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        //update request
        competition1.setMaxParticipants(2);
        competitionResponse = testRestTemplate.exchange("/competitions",
                        HttpMethod.PUT,
                        createCompetitionEntity(competition1, authorizationHeader1),
                        Competition.class);

        competition1 = competitionResponse.getBody();
        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(competition1.getMaxParticipants()).isEqualTo(2);

        //second assign
        String authorizationHeader4 = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user3);
        HttpHeaders headers4 = new HttpHeaders();
        headers4.setContentType(MediaType.APPLICATION_JSON);
        headers4.set(HEADER_STRING, authorizationHeader4);
        HttpEntity<String> entity4 = new HttpEntity<>(headers4);
        competitionResponse =
                testRestTemplate.postForEntity("/competitions/" + competitionId, entity4, Competition.class);

        assertThat(competitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(competitionResponse.getBody().getParticipantsCounter()).isEqualTo(2);
    }

    private HttpEntity createCompetitionEntity(Competition competition, String authorizationHeader) {
        String json = jsonUtil.toJson(competition);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_STRING, authorizationHeader);
        return new HttpEntity<>(json, headers);
    }
}
