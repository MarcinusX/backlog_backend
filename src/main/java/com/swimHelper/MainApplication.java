package com.swimHelper;

import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User user = new User();
        user.setEmail("admin@admin.pl");
        user.setPassword("admin");
        user = userService.addUser(user);
        userService.makeUserAdmin(user.getId());

        User user2 = new User();
        user2.setEmail("dowlny2@email.com");
        user2.setPassword("abcdef2");
        userService.addUser(user2);

        userService.getAll().forEach(System.out::println);
    }
}
