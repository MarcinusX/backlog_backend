package com.swimHelper.service;

import com.swimHelper.aspect.TimeMeasured;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.InvalidUserException;
import com.swimHelper.exception.UserExistsException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.Role;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param user user to be added
     * @return added user
     */
    @TimeMeasured
    public User addUser(User user) throws BusinessException {
        checkUserAddingPrerequisites(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        try {
            return userRepository.saveAndFlush(user);
        } catch (ConstraintViolationException e) {
            throw new InvalidUserException(e);
        }
    }

    public User makeUserAdmin(Long userId) {
        User user = getUser(userId);
        Set<Role> userRoles = new HashSet<>(user.getRoles());
        userRoles.add(Role.ADMIN);
        user.setRoles(userRoles);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }

    public User updateUser(User user) throws BusinessException {
        User userFromRepo = getUserForUpdate(user);
        User userToUpdate = copyUpdatableFields(userFromRepo, user);
        try {
            return userRepository.saveAndFlush(userToUpdate);
        } catch (ConstraintViolationException e) {
            throw new InvalidUserException(e);
        }
    }

    private User copyUpdatableFields(User userFromDB, User userFromRequest) {
        userFromDB.setFirstname(userFromRequest.getFirstname());
        userFromDB.setLastname(userFromRequest.getLastname());
        userFromDB.setEmail(userFromRequest.getEmail());
        userFromDB.setWeight(userFromRequest.getWeight());
        userFromDB.setStyleStatistics(userFromRequest.getStyleStatistics());
        return userFromDB;
    }

    private User getUserForUpdate(User user) throws BusinessException {
        if (user == null || user.getId() == null) {
            throw new InvalidUserException("Invalid user");
        }
        User userFromRepo = userRepository.findOne(user.getId());
        if (userFromRepo == null) {
            throw new UserNotFoundException("Could not find user with id: " + user.getId());
        }
        return userFromRepo;
    }

    private void checkUserAddingPrerequisites(User user) throws BusinessException {
        if (user.getId() != null) {
            throw new InvalidUserException("User cannot have id property");
        } else if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserExistsException("User with that email already exists");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }
}
