package com.swimHelper.controller.user;

import com.swimHelper.TestUtil;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@ActiveProfiles("test")
public class UserControllerStatusCodesTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void before() {
        userRepository.deleteAll();
    }

    @Test
    public void getUser_ReturnsUserWithStatusOk() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        User addedUser = userRepository.saveAndFlush(user);
        //when
        ResponseEntity<User> responseEntity =
                testRestTemplate.getForEntity("/users/" + addedUser.getId(), User.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUser_whenUserMissing_returns404() throws Exception {
        //given empty repo

        //when
        ResponseEntity<User> responseEntity = testRestTemplate.getForEntity("/users/1", User.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void addUser_whenUserHasId_returns400() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        user.setId(1L);
        //when
        ResponseEntity<User> responseEntity = testRestTemplate.postForEntity("/users", user, User.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addUser_whenUserExists_returns409() throws Exception {
        //given
        User savedUser = TestUtil.createValidUser();
        savedUser.setEmail("some@email.com");
        userRepository.saveAndFlush(savedUser);
        User userToRequest = TestUtil.createValidUser();
        userToRequest.setEmail("some@email.com");
        //when
        ResponseEntity<User> responseEntity = testRestTemplate.postForEntity("/users", userToRequest, User.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
