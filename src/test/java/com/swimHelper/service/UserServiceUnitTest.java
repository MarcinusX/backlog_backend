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

    @Test
    public void getUser_shouldReturnFromRepo() throws Exception {
        //given
        User user = new User();
        when(userRepositoryMock.findOne(1L)).thenReturn(user);
        //when
        User returnedUser = sut.getUser(1L);
        //then
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void getUser_ifRepoGivesNull_shouldReturnNull() throws Exception {
        //given
        when(userRepositoryMock.getOne(1L)).thenReturn(null);
        //when
        User returnedUser = sut.getUser(1L);
        //then
        assertThat(returnedUser).isNull();
    }

}