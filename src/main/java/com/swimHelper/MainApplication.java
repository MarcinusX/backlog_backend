package com.swimHelper;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class MainApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addInitialDataToDatabase();
    }

    private void addInitialDataToDatabase() throws BusinessException {
        User user = new User();
        user.setEmail("admin@admin.pl");
        user.setPassword("admin");
        user = userService.addUser(user);

        User user1 = new User();
        user1.setEmail("user@user.pl");
        user1.setPassword("admin");
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 180));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 300));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 280));
        user1.setStyleStatistics(styleStatistics);
        user1 = userService.addUser(user1);



        userService.makeUserAdmin(user.getId());

        Training training = new Training();
        training.setNotificationDateTime(LocalDateTime.now());
        training.setTrainingDateTime(LocalDateTime.now().plusMinutes(30));
        training.setUser(user);
        //trainingRepository.saveAndFlush(training);

        trainingRepository.findAll().forEach(System.out::println);
        userService.getAll().forEach(System.out::println);

        for (int i = 0; i < 6; i++) {
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setDescription("desc");
            exercise.setName("name" + i);
            if (i > 2) {
                exercise.setWarmUpRelax(false);
            } else {
                exercise.setWarmUpRelax(true);
            }
            exerciseRepository.save(exercise);
        }

        for(int i = 0; i <10; i++) {
            Training training1 = new Training();
            training1.setUser(user1);
            if(i < 3) {
                training1.setNotificationDateTime(LocalDateTime.now().plusDays(i + 1));
                training1.setTrainingDateTime(LocalDateTime.now().plusMonths(i + 1).minusHours(1));
            } else {
                training1.setNotificationDateTime(LocalDateTime.now().minusMonths(i - 2));
                training1.setTrainingDateTime(LocalDateTime.now().minusMonths(i - 2).minusHours(1));
            }

            List<ExerciseSeries> series = new ArrayList<>();
            ExerciseSeries exerciseSeries = new ExerciseSeries();
            exerciseSeries.setRepeats(i);
            if(i >=3 && i < 7) {
                exerciseSeries.setCompletedPercentage(i * 10);
            }
            series.add(exerciseSeries);
            training1.setExerciseSeries(series);
            training1.setCompletedPercentage(i * 10);
            trainingRepository.save(training1);
        }
    }
}
