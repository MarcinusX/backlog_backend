package com.swimHelper;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.*;
import com.swimHelper.service.UserService;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
@Component
public class TestUtil {

    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private UserService userService;

    public User createValidUser() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 120));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 100));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 230));

        User user = new User();
        user.setWeight(65);
        user.setStyleStatistics(styleStatistics);
        user.setEmail("some@email.com");
        user.setPassword("somePassword");
        return user;
    }

    public User createValidUserWithWeakStatistics() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 180));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 300));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 280));

        User user = new User();
        user.setWeight(65);
        user.setStyleStatistics(styleStatistics);
        user.setEmail("some@email.com");
        user.setPassword("somePassword");
        return user;
    }

    public ResponseEntity<User> postUser(TestRestTemplate testRestTemplate, User user) {
        String json = jsonUtil.toJson(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.postForEntity("/users", entity, User.class);
    }

    public ResponseEntity<User> putUser(TestRestTemplate testRestTemplate, User user) {
        String json = jsonUtil.toJson(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);
    }

    public User addUser(TestRestTemplate testRestTemplate) {
        User user = createValidUser();
        ResponseEntity<User> responseEntity = postUser(testRestTemplate, user);
        return responseEntity.getBody();
    }

    public TrainingRequirements createValidTrainingRequirements() {
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.FREESTYLE);
        styles.add(Style.BACKSTROKE);
        LocalDateTime trainingDateTime = LocalDateTime.of(2100, 11, 20, 6, 40, 45);
        LocalDateTime notificationDateTime = LocalDateTime.of(2100, 11, 20, 5, 40, 45);
        return new TrainingRequirements(styles, IntensityLevel.LOW, 3000, 1000, trainingDateTime, notificationDateTime);
    }

    public Training createValidTraining() {
        Training training = new Training();
        List<ExerciseSeries> exerciseSeriesList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ExerciseSeries exerciseSeries = new ExerciseSeries();
            Exercise exercise = new Exercise(Style.FREESTYLE);
            exercise.setName("exercise" + i);
            exerciseSeries.setRepeats(5);
            exerciseSeries.setDistance((i + 1) * 100);
            exerciseSeriesList.add(exerciseSeries);
        }

        training.setExerciseSeries(exerciseSeriesList);
        return training;
    }

    public User createAdminForTests() throws BusinessException {
        User admin = new User();
        admin.setEmail(TrainingTestUtil.ADMIN_EMAIL);
        admin.setPassword(TrainingTestUtil.ADMIN_PASSWORD);
        admin = userService.addUser(admin);
        userService.makeUserAdmin(admin.getId());
        return admin;
    }
}
