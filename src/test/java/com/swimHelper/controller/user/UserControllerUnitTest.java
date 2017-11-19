package com.swimHelper.controller.user;

import com.swimHelper.controller.UserController;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserControllerUnitTest {

    private final UserService userServiceMock = mock(UserService.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final UserController sut = new UserController(userServiceMock, userDetailsService);

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

    @Test
    public void getUser_shouldReturnFromService() throws Exception {
        //given
        User user = new User();
        when(userServiceMock.getUser(1L)).thenReturn(user);
        //when
        User returnedUser = sut.getUser(1L);
        //then
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void getUser_whenServiceGivesNull_shouldThrowException() throws Exception {
        //given
        when(userServiceMock.getUser(1L)).thenReturn(null);
        //when
        Throwable throwable = catchThrowable(() -> sut.getUser(1L));
        //then
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void putUser_shouldReturnFromService() throws Exception {
        //given
        User user = new User();
        when(userServiceMock.updateUser(any())).thenReturn(user);
        //when
        User returnedUser = sut.putUser(new User());
        //then
        assertThat(returnedUser).isEqualTo(user);
    }
}
