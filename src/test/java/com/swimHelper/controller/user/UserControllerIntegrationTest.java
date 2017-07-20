package com.swimHelper.controller.user;

import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
public class UserControllerIntegrationTest {

    @MockBean
    private UserService userServiceMock;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void getUser_ReturnsUserWithStatusOk() throws Exception {
        //given
        when(userServiceMock.getUser(1L)).thenReturn(new User());
        //when
        ResponseEntity<User> allPlayersResponse = testRestTemplate.getForEntity("/users/1", User.class);
        //then
        assertThat(allPlayersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUser_whenUserMissing_returns404() throws Exception {
        //given
        when(userServiceMock.getUser(1L)).thenReturn(null);
        //when
        ResponseEntity<User> allPlayersResponse = testRestTemplate.getForEntity("/users/1", User.class);
        //then
        assertThat(allPlayersResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
