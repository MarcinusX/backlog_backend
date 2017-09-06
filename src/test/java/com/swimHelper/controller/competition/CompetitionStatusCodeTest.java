package com.swimHelper.controller.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.controller.CompetitionController;
import com.swimHelper.exception.*;
import com.swimHelper.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created by Marcin Szalek on 06.09.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@ActiveProfiles("security")
public class CompetitionStatusCodeTest {

    @MockBean
    CompetitionController competitionControllerMock;

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    TestUtil testUtil;
    @Autowired
    CompetitionTestUtil competitionTestUtil;
    @Autowired
    UserRepository userRepository;

    @Before
    public void addUser() {
        userRepository.deleteAll();
        testUtil.postUser(testRestTemplate, testUtil.createValidUser());
    }

    @Test
    public void competitionExpiredException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new CompetitionExpiredException());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/1", HttpMethod.POST, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void competitionFullException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new CompetitionFullException());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/1", HttpMethod.POST, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void competitionNotFoundException_resultsIn404() throws Exception {
        //given
        when(competitionControllerMock.getCompetition(anyLong()))
                .thenThrow(new CompetitionNotFoundException());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/1", HttpMethod.GET, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void invalidCompetitionException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.addCompetition(any()))
                .thenThrow(new InvalidCompetitionException(new Throwable()));
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions", HttpMethod.POST, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void optimisticLockException_resultsIn409() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new OptimisticLockException());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/1", HttpMethod.POST, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void userAlreadySignedToCompetition_resultsIn409() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new UserAlreadySignedToCompetition());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/1", HttpMethod.POST, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void userNotSignedToCompetition_resultsIn409() throws Exception {
        //given
        doThrow(new UserNotSignedToCompetition())
                .when(competitionControllerMock).leaveCompetition(anyLong());
        //when
        ResponseEntity<Object> response = testRestTemplate.withBasicAuth("some@email.com", "somePassword")
                .exchange("/competitions/leave/1", HttpMethod.DELETE, null, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

}
