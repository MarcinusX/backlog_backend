package com.swimHelper.controller.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.repository.CompetitionRepository;
import com.swimHelper.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CompetitionEndToEndTest {

    @Autowired
    TestUtil testUtil;
    @Autowired
    CompetitionTestUtil competitionTestUtil;
    @Autowired
    TestRestTemplate testRestTemplate;

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
        ResponseEntity<Competition> competitionResponseEntity = testRestTemplate.postForEntity("/competitions", competition, Competition.class);
        long competitionId = competitionResponseEntity.getBody().getId();
        //assignToCompetition
        ResponseEntity<Competition> compEntity = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .postForEntity("/competitions/leave/" + competitionId, null, Competition.class);

        assertThat(compEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
