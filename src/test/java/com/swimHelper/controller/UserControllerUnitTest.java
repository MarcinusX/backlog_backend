package com.swimHelper.controller;

import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserControllerUnitTest {

    private final UserService userServiceMock = mock(UserService.class);
    private final UserController sut = new UserController(userServiceMock);

    @Test
    public void addUser_shouldReturnAddedUser() throws Exception {
        //given
        User user = new User();
        User userSaved = new User();
        when(userServiceMock.addUser(user)).thenReturn(userSaved);
        //when
        User returnedUser = sut.postUser(user);
        //then
        assertThat(returnedUser).isEqualTo(userSaved);
    }
}
