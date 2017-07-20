package com.swimHelper.controller.user;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
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
public class UserControllerEndToEndTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void addAndGetUser() throws Exception {
        //given
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("some@email.com");
        user.setPassword("12345");
        //when
        ResponseEntity<User> user1 = testRestTemplate.postForEntity("/users", user, User.class);
        ResponseEntity<User> user2 = testRestTemplate.getForEntity("/users/" + user1.getBody().getId(), User.class);
        //then
        assertThat(user2.getBody().getEmail()).isEqualTo("some@email.com");
        //TODO: password should not be visible
    }
}
