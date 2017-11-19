package com.swimHelper.security;

import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.UserService;
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

import java.util.Collections;

import static com.swimHelper.security.SecurityConstants.HEADER_STRING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 25.07.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class UserSecurityIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TrainingTestUtil trainingTestUtil;

    @Before
    public void init() {
        userRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void whenUserAdded_passwordIsEncrypted() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        //when
        testUtil.postUser(testRestTemplate, user);
        User userFromRepo = userRepository.findByEmail("some@email.com");
        //then
        assertThat(userFromRepo.getPassword()).isNotEqualTo("12345");
    }

    @Test
    public void unauthorizedUserCantAccessUserData() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        ResponseEntity<User> createdUserEntity = testRestTemplate.postForEntity("/users", user, User.class);
        //when
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<User> userResponseEntity =
                testRestTemplate.exchange("/users/" + createdUserEntity.getBody().getId(), HttpMethod.GET, httpEntity, User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void userCanAccessOwnUserData() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        ResponseEntity<User> createdUserEntity = testUtil.postUser(testRestTemplate, user);
        JwtUser jwtUser = new JwtUser("some@email.com", "12345");
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        //when
        ResponseEntity<User> userResponseEntity =
                testRestTemplate.exchange("/users/" + createdUserEntity.getBody().getId(), HttpMethod.GET, httpEntity, User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponseEntity.getBody()).isNotNull();
    }

    @Test
    public void userCantAccessOtherUserData() throws Exception {
        //given
        User user1 = createUser("user1@email.com", "pass1");
        User user2 = createUser("user2@email.com", "pass2");
        ResponseEntity<User> createdUserEntity1 = testUtil.postUser(testRestTemplate, user1);
        ResponseEntity<User> createdUserEntity2 = testUtil.postUser(testRestTemplate, user2);
        //when
        ResponseEntity<User> userResponseEntity =
                testRestTemplate
                        .withBasicAuth("user1@email.com", "pass1")
                        .getForEntity("/users/" + createdUserEntity2.getBody().getId(), User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void userCanUpdateOwnUserData() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        ResponseEntity<User> createdUserEntity = testUtil.postUser(testRestTemplate, user);
        user = createdUserEntity.getBody();
        user.setWeight(23.0);
        JwtUser jwtUser = new JwtUser("some@email.com", "12345");
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser);
        //when
        ResponseEntity<User> userResponseEntity = testUtil.putUser(testRestTemplate, user, authorizationHeader);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponseEntity.getBody()).isNotNull();
    }

    @Test
    public void userCantUpdateOtherUserData() throws Exception {
        //given
        User user1 = createUser("user1@email.com", "pass1");
        User user2 = createUser("user2@email.com", "pass2");
        ResponseEntity<User> createdUserEntity1 = testUtil.postUser(testRestTemplate, user1);
        ResponseEntity<User> createdUserEntity2 = testUtil.postUser(testRestTemplate, user2);
        user2 = createdUserEntity2.getBody();
        user2.setStyleStatistics(Collections.emptyList());
        JwtUser jwtUser = new JwtUser("user1@email.com", "pass1");
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser);
        //when
        ResponseEntity<User> userResponseEntity =
                testUtil.putUser(testRestTemplate, user2, authorizationHeader);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void userCannotGetAllUsers() throws Exception {
        //given
        User user = createUser("user1@email.com", "pass1");
        testUtil.postUser(testRestTemplate, user);
        //when
        ResponseEntity usersResponseEntity =
                testRestTemplate
                        .withBasicAuth("user1@email.com", "pass1")
                        .getForEntity("/users", Object.class);
        //then
        assertThat(usersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void adminCanGetAllUsers() throws Exception {
        //given
        User user = createUser("user1@email.com", "pass1");
        User body = testUtil.postUser(testRestTemplate, user).getBody();
        userService.makeUserAdmin(body.getId());
        JwtUser jwtUser = new JwtUser("user1@email.com", "pass1");
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_STRING, authorizationHeader);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        //when
        ResponseEntity usersResponseEntity =
                testRestTemplate
                        .exchange("/users/", HttpMethod.GET, httpEntity, Object.class);
        //then
        assertThat(usersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void userCannotMakeAUserAdmin() throws Exception {
        //given
        User user = createUser("user1@email.com", "pass1");
        user = testUtil.postUser(testRestTemplate, user).getBody();
        //when
        ResponseEntity usersResponseEntity =
                testRestTemplate
                        .withBasicAuth("user1@email.com", "pass1")
                        .exchange("/users/admin/" + user.getId() + "/makeAdmin", HttpMethod.PUT, null, User.class);
        //then
        assertThat(usersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void AdminCanMakeAUserAdmin() throws Exception {
        //given
        User user = createUser("user1@email.com", "pass1");
        user = testUtil.postUser(testRestTemplate, user).getBody();
        userService.makeUserAdmin(user.getId());
        JwtUser jwtUser = new JwtUser("user1@email.com", "pass1");
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, jwtUser);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        //when
        ResponseEntity usersResponseEntity =
                testRestTemplate.exchange("/users/admin/" + user.getId() + "/makeAdmin", HttpMethod.PUT, httpEntity, User.class);
        //then
        assertThat(usersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

}