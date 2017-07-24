package com.swimHelper.service;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.InvalidUserException;
import com.swimHelper.exception.UserExistsException;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@Service
public class UserService {

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
    public User addUser(User user) throws BusinessException {
        checkPrerequisites(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }

    private void checkPrerequisites(User user) throws BusinessException {
        if (user.getId() != null) {
            throw new InvalidUserException("User cannot have id property");
        } else if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserExistsException("User with that email already exists");
        }
    }
}
