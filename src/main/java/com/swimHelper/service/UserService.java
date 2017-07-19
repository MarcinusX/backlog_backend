package com.swimHelper.service;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param user user to be added
     * @return added user
     */
    public User addUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }
}
