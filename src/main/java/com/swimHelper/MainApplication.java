package com.swimHelper;

import com.swimHelper.model.User;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;

@SpringBootApplication
@EnableScheduling
public class MainApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User user = new User();
        user.setEmail("dowlny@email.com");
        user.setPassword("abcdef");
		userRepository.saveAndFlush(user);

        User user2 = new User();
        user2.setEmail("dowlny2@email.com");
        user2.setPassword("abcdef2");
        userRepository.saveAndFlush(user2);

        userRepository.findAll().forEach(System.out::println);
    }
}
