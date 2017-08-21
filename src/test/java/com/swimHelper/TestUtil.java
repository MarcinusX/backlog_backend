package com.swimHelper;

import com.swimHelper.model.Style;
import com.swimHelper.model.StyleStatistics;
import com.swimHelper.model.User;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import com.swimHelper.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
@Component
public class TestUtil {

    @Autowired
    JsonUtil jsonUtil;

    public User createValidUser() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 120));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 100));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 230));
        styleStatistics.add(new StyleStatistics(Style.INDIVIDUAL_MEDLEY, 100, 140));

        User user = new User();
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

    public static TrainingRequirements createValidTrainingRequirements() {
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.FREESTYLE);
        styles.add(Style.BACKSTROKE);
        Collection<Equipment> availableEquipment = new ArrayList<>();
        availableEquipment.add(Equipment.TEMP);
        Collection<TrainingPurpose> trainingPurposes = new ArrayList<>();
        trainingPurposes.add(TrainingPurpose.IMPROVE_RECORDS);
        return new TrainingRequirements(styles, DifficultyLevel.BEGINNER, IntensityLevel.LOW, 600, 1000,
                availableEquipment, trainingPurposes);
    }

    public static Training createValidTraining() {
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
}
