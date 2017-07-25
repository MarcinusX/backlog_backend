package com.swimHelper.service;

import com.swimHelper.exception.InvalidUserException;
import com.swimHelper.exception.UserExistsException;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserServiceUnitTest {

    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
    final UserService sut = new UserService(userRepositoryMock, passwordEncoderMock);

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
    public void addUser_whenUserHasId_shouldThrowException() throws Exception {
        //given
        User user = new User();
        user.setId(1L);
        when(userRepositoryMock.findOne(1L)).thenReturn(new User());
        //when
        Throwable thrown = catchThrowable(() -> sut.addUser(user));
        //then
        assertThat(thrown).isInstanceOf(InvalidUserException.class);
        assertThat(thrown).hasMessage("User cannot have id property");
    }

    @Test
    public void addUser_whenUserExists_shouldThrowException() throws Exception {
        //given
        User user = new User();
        user.setEmail("email@email.com");
        when(userRepositoryMock.findByEmail("email@email.com")).thenReturn(new User());
        //when
        Throwable thrown = catchThrowable(() -> sut.addUser(user));
        //then
        assertThat(thrown).isInstanceOf(UserExistsException.class);
        assertThat(thrown).hasMessage("User with that email already exists");
    }

    @Test
    public void addUser_shouldEncryptPassword() throws Exception {
        //given
        User user = new User();
        user.setPassword("123abc");
        user.setEmail("some@email.com");
        when(userRepositoryMock.saveAndFlush(user)).thenReturn(user);
        when(passwordEncoderMock.encode(anyString())).thenReturn("encryptedPassword");
        //when
        User returnedUser = sut.addUser(user);
        //then
        verify(passwordEncoderMock).encode("123abc");
        assertThat(returnedUser.getPassword()).isEqualTo("encryptedPassword");
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