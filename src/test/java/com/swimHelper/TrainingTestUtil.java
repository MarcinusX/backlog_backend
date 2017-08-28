package com.swimHelper;

import com.swimHelper.model.*;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@Component
public class TrainingTestUtil {

    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private TestUtil testUtil;

    public ResponseEntity<Exercise> postExercise(TestRestTemplate testRestTemplate, Exercise exercise) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.postForEntity("/exercises", entity, Exercise.class);
    }

    public ResponseEntity<Exercise> putExercise(TestRestTemplate testRestTemplate, Exercise exercise) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.exchange("/exercises", HttpMethod.PUT, entity, Exercise.class);
    }

    public ResponseEntity<Training> postTrainingRequirements(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements) {
        String json = jsonUtil.toJson(trainingRequirements);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.withBasicAuth("some@email.com", "somePassword").
                exchange("/trainings", HttpMethod.POST, entity, Training.class);
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

            ResponseEntity<Exercise> responseEntity = postExerciseWithAuth(testRestTemplate, exercise);
        }
    }

    private ResponseEntity<Exercise> postExerciseWithAuth(TestRestTemplate testRestTemplate, Exercise exercise) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.withBasicAuth("some@email.com", "somePassword").postForEntity("/exercises", entity, Exercise.class);
    }

    public void addUser(TestRestTemplate testRestTemplate) {
        User user = testUtil.createValidUser();
        testUtil.postUser(testRestTemplate, user);
    }
}
