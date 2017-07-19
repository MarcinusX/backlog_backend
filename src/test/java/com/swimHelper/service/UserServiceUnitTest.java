package com.swimHelper.service;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserServiceUnitTest {

    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final UserService sut = new UserService(userRepositoryMock);

    @Test
    public void addUser_shouldReturnAddedUser() throws Exception {
        //given
        User user = new User();
        User userSaved = new User();
        when(userRepositoryMock.saveAndFlush(user)).thenReturn(userSaved);
        //when
        User returnedUser = sut.addUser(user);
        //then
        assertThat(returnedUser).isEqualTo(userSaved);
    }
}