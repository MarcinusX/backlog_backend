package com.swimHelper.security;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
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
 * Created by Marcin Szalek on 25.07.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class SecurityIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void init() {
        userRepository.deleteAll();
    }

    @Test
    public void whenUserAdded_passwordIsEncrypted() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        //when
        testRestTemplate.postForEntity("/users", user, User.class);
        User userFromRepo = userRepository.findByEmail("some@email.com");
        //then
        assertThat(userFromRepo.getPassword()).isNotEqualTo("12345");
    }

    @Test
    public void unauthenticatedUserCantAccessUserData() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        ResponseEntity<User> createdUserEntity = testRestTemplate.postForEntity("/users", user, User.class);
        //when
        ResponseEntity<User> userResponseEntity =
                testRestTemplate.getForEntity("/users/" + createdUserEntity.getBody().getId(), User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void userCanAccessOwnUserData() throws Exception {
        //given
        User user = createUser("some@email.com", "12345");
        ResponseEntity<User> createdUserEntity = testRestTemplate.postForEntity("/users", user, User.class);
        //when
        ResponseEntity<User> userResponseEntity =
                testRestTemplate
                        .withBasicAuth("some@email.com", "12345")
                        .getForEntity("/users/" + createdUserEntity.getBody().getId(), User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponseEntity.getBody()).isNotNull();
    }

    @Test
    public void userCantAccessOtherUserData() throws Exception {
        //given
        User user1 = createUser("user1@email.com", "pass1");
        User user2 = createUser("user2@email.com", "pass2");
        ResponseEntity<User> createdUserEntity1 = testRestTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> createdUserEntity2 = testRestTemplate.postForEntity("/users", user2, User.class);
        //when
        ResponseEntity<User> userResponseEntity =
                testRestTemplate
                        .withBasicAuth("user1@email.com", "pass1")
                        .getForEntity("/users/" + createdUserEntity2.getBody().getId(), User.class);
        //then
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

}