package com.swimHelper.service.user;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.InvalidUserException;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by Marcin Szalek on 09.08.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService sut;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtil testUtil;

    @Before
    public void init() {
        userRepository.deleteAll();
    }

    @Test
    public void updateUser_whenUserHasNullEmail_shouldThrowException() throws Exception {
        //given
        User user = testUtil.createValidUser();
        user.setId(1L);
        User createdUser = userRepository.saveAndFlush(user);
        createdUser.setEmail(null);
        //when
        Throwable thrown = catchThrowable(() -> sut.updateUser(createdUser));
        //then
        assertThat(thrown).isInstanceOf(InvalidUserException.class);
    }
}
