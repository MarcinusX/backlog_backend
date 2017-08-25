package com.swimHelper;

import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class MainApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainingRepository trainingRepository;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User user = new User();
        user.setFirstname("Monia");
        user.setEmail("example@somemail.com");
        user.setPassword("abcdef");
        //user = userRepository.saveAndFlush(user);

        Training training = new Training();
        training.setNotificationDateTime(LocalDateTime.now());
        training.setDateTime(LocalDateTime.now().plusMinutes(30));
        training.setUser(user);
        //trainingRepository.saveAndFlush(training);

        trainingRepository.findAll().forEach(System.out::println);
        userRepository.findAll().forEach(System.out::println);
    }
}
