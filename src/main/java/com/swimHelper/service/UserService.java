package com.swimHelper.service;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.InvalidUserException;
import com.swimHelper.exception.UserExistsException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public User addUser(User user) throws BusinessException {
        checkUserAddingPrerequisites(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }

    public User updateUser(User user) throws BusinessException {
        if (user == null || user.getId() == null) {
            throw new InvalidUserException("Invalid user");
        }
        User userFromRepo = userRepository.findOne(user.getId());
        if (userFromRepo == null) {
            throw new UserNotFoundException("Could not find user with id: " + user.getId());
        }
        user.setPassword(userFromRepo.getPassword());
        return userRepository.saveAndFlush(user);
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
