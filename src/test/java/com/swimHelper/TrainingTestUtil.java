package com.swimHelper;

import com.swimHelper.model.*;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@Component
public class TrainingTestUtil {

    public final static String ADMIN_PASSWORD = "admin";
    public final static String ADMIN_EMAIL = "admin@admin.pl";
    public final static String USER_EMAIL = "some@email.com";
    public final static String USER_PASSWORD = "somePassword";
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private TestUtil testUtil;

    public ResponseEntity<Exercise> postExercise(TestRestTemplate testRestTemplate, Exercise exercise, String email, String password) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        if (email == null || password == null) {
            return testRestTemplate.postForEntity("/exercises", entity, Exercise.class);
        } else {
            return testRestTemplate.withBasicAuth(email, password).postForEntity("/exercises", entity, Exercise.class);
        }
    }

    public ResponseEntity<Exercise> putExercise(TestRestTemplate testRestTemplate, Exercise exercise, String email, String password) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        if (email == null || password == null) {
            return testRestTemplate.exchange("/exercises", HttpMethod.PUT, entity, Exercise.class);
        } else {
            return testRestTemplate.withBasicAuth(email, password).exchange("/exercises", HttpMethod.PUT, entity, Exercise.class);
        }
    }

    public ResponseEntity<Exercise> getExercise(TestRestTemplate testRestTemplate, Long id, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if (email == null || password == null) {
            return testRestTemplate.exchange("/exercises/" + id, HttpMethod.GET, entity, Exercise.class);
        } else {
            return testRestTemplate.withBasicAuth(email, password).exchange("/exercises/" + id, HttpMethod.GET, entity, Exercise.class);
        }
    }

    public ResponseEntity<Training> postTrainingRequirements(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements) {
        String json = jsonUtil.toJson(trainingRequirements);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.withBasicAuth(USER_EMAIL, USER_PASSWORD).
                exchange("/trainings", HttpMethod.POST, entity, Training.class);
    }

    public ResponseEntity<Training> putTraining(TestRestTemplate testRestTemplate, Training training) {
        String json = jsonUtil.toJson(training);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.withBasicAuth(USER_EMAIL, USER_PASSWORD).
                exchange("/trainings", HttpMethod.PUT, entity, Training.class);
    }

    public void addExercises(TestRestTemplate testRestTemplate) {
        for (int i = 0; i < 6; i++) {
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setDescription("desc");
            exercise.setName("name" + i);
            if (i > 2) {
                exercise.setWarmUpRelax(false);
            } else {
                exercise.setWarmUpRelax(true);
            }

            ResponseEntity<Exercise> responseEntity = postExercise(testRestTemplate, exercise, ADMIN_EMAIL, ADMIN_PASSWORD);
        }
    }

    public User addUser(TestRestTemplate testRestTemplate) {
        User user = testUtil.createValidUser();
        ResponseEntity<User> responseEntity = testUtil.postUser(testRestTemplate, user);
        return responseEntity.getBody();
    }

    public Training createValidTraining() {
        Training training = new Training();
        training.setId(1L);
        List<ExerciseSeries> exerciseSeriesList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ExerciseSeries exerciseSeries = new ExerciseSeries();
            exerciseSeries.setId((long) i);
            exerciseSeries.setRepeats(3);
            exerciseSeries.setDurationOfOneExerciseInSeconds(600);
            exerciseSeries.setBreakInSeconds(40);
            exerciseSeries.setDistance(300);
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setName("name" + i);
            exercise.setId((long) i);
            exercise.setDescription("desc" + i);
            if (i < 3) {
                exercise.setWarmUpRelax(true);
            } else {
                exercise.setWarmUpRelax(false);
            }
            exerciseSeries.setExercise(exercise);
            exerciseSeriesList.add(exerciseSeries);
        }
        training.setExerciseSeries(exerciseSeriesList);
        User user = new User();
        user.setId(1L);
        user.setEmail("some@email.com");
        user.setPassword("somePassword");
        training.setUser(user);
        return training;
    }
}
