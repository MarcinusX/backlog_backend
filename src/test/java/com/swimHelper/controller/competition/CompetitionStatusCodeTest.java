package com.swimHelper.controller.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.controller.CompetitionController;
import com.swimHelper.exception.*;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.security.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.swimHelper.security.SecurityConstants.HEADER_STRING;
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
    TrainingTestUtil trainingTestUtil;
    @Autowired
    UserRepository userRepository;

    @Before
    public void addUser() {
        testUtil.postUser(testRestTemplate, testUtil.createValidUser());
    }

    @Test
    public void competitionExpiredException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new CompetitionExpiredException());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/1", HttpMethod.POST, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void competitionFullException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new CompetitionFullException());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/1", HttpMethod.POST, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void competitionNotFoundException_resultsIn404() throws Exception {
        //given
        when(competitionControllerMock.getCompetition(anyLong()))
                .thenThrow(new CompetitionNotFoundException());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/1", HttpMethod.GET, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void invalidCompetitionException_resultsIn400() throws Exception {
        //given
        when(competitionControllerMock.addCompetition(any()))
                .thenThrow(new InvalidCompetitionException(new Throwable()));
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions", HttpMethod.POST, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void optimisticLockException_resultsIn409() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new OptimisticLockException());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/1", HttpMethod.POST, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void userAlreadySignedToCompetition_resultsIn409() throws Exception {
        //given
        when(competitionControllerMock.assignToCompetition(anyLong()))
                .thenThrow(new UserAlreadySignedToCompetition());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/1", HttpMethod.POST, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void userNotSignedToCompetition_resultsIn409() throws Exception {
        //given
        doThrow(new UserNotSignedToCompetition())
                .when(competitionControllerMock).leaveCompetition(anyLong());
        HttpEntity<String> entity = createHttpEntity();
        //when
        ResponseEntity<Object> response = testRestTemplate.exchange("/competitions/leave/1", HttpMethod.DELETE, entity, Object.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private HttpEntity<String> createHttpEntity() {
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_STRING, authorizationHeader);
        return new HttpEntity<>(httpHeaders);
    }

}
