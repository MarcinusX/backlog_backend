package com.swimHelper.initializer;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by marcinus on 19.04.17.
 */
@Component
@PropertySource("classpath:application-test.properties")
public class InitializeDatabase {

    @Value("${test.db.initializer.users.size}")
    private Integer usersSize;

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void initalizeDB() {
        userRepository.deleteAll();
        for (int i = 0; i < usersSize; i++) {
            User user = new User();
            user.setEmail("email@cos.pl" + i);
            user.setPassword("pass" + i);
            userRepository.saveAndFlush(user);
        }
    }

}