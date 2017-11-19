package com.swimHelper.controller.user;

import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
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

    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TrainingTestUtil trainingTestUtil;

    @Test
    public void addUpdateAndGetUser() throws Exception {
        //given
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("some@email.com");
        user.setPassword("12345");
        //when
        ResponseEntity<User> responseEntity1 = testUtil.postUser(testRestTemplate, user);
        User userToUpdate = responseEntity1.getBody();
        userToUpdate.setWeight(75.0);
        ResponseEntity<User> responseEntity2 = testUtil.putUser(testRestTemplate, userToUpdate, null);
        ResponseEntity<User> responseEntity3 = testRestTemplate.getForEntity("/users/" + userToUpdate.getId(), User.class);
        //then
        assertThat(responseEntity3.getBody().getEmail()).isEqualTo("some@email.com");
        assertThat(responseEntity3.getBody().getWeight()).isEqualTo(75.0);
        assertThat(responseEntity3.getBody().getPassword()).isNull();
    }
}
