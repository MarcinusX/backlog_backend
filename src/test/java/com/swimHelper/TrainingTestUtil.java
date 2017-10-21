package com.swimHelper;

import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private ExerciseRepository exerciseRepository;

    //*********************END TO END TESTS METHODS********************************//

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

    public ResponseEntity<IntegerWrapper> countDistance(TestRestTemplate testRestTemplate,
                                                        Long trainingId,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        StringBuilder url = new StringBuilder().append("/trainings?");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        url = addParamsToUrl(url, trainingId, startDate, endDate);

        return testRestTemplate.withBasicAuth(USER_EMAIL, USER_PASSWORD)
                .exchange(url.toString(), HttpMethod.GET, entity, IntegerWrapper.class);
    }

    public ResponseEntity<IntegerWrapper> calculateCalories(TestRestTemplate testRestTemplate,
                                                            Long trainingId,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        StringBuilder url = new StringBuilder().append("/calories?");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        url = addParamsToUrl(url, trainingId, startDate, endDate);

        return testRestTemplate.withBasicAuth(USER_EMAIL, USER_PASSWORD)
                .exchange(url.toString(), HttpMethod.GET, entity, IntegerWrapper.class);
    }

    //*****************TRAININGS******************//

    public void addTrainings(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements) {
        for (int i = 0; i < 5; i++) {
            addTraining(testRestTemplate, trainingRequirements);
        }
    }

    public Training addTraining(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements) {
        Training training = postTrainingRequirements(testRestTemplate, trainingRequirements).getBody();
        training.getExerciseSeries().forEach(es -> {
            es.setCompletedRepeats(3);
            es.setAverageDurationOfOneRepeatInSeconds(300);
        });
        return putTraining(testRestTemplate, training).getBody();
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

    //*****************EXERCISES******************//

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
            exercise.getRequiredTrainingEquipment().add(TrainingEquipment.FINS);
            exercise.getRequiredTrainingEquipment().add(TrainingEquipment.PADDLES);

            ResponseEntity<Exercise> responseEntity = postExercise(testRestTemplate, exercise, ADMIN_EMAIL, ADMIN_PASSWORD);
        }
    }

    public void addExercisesInSpecifiedStyle(Style style) {
        String description = "description";
        for (int i = 0; i < 12; i++) {
            Exercise exercise = new Exercise(style);
            exercise.setName(style.name() + i);
            exercise.setDescription(description + i);
            saveExercise(exercise);
        }
    }

    public void addWarmUpRelaxExercises() {
        String name = "name";
        String description = "description";
        for (int i = 13; i < 17; i++) {
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setName(Style.BACKSTROKE.name() + i);
            exercise.setDescription(description + i);
            exercise.setWarmUpRelax(true);
            saveExercise(exercise);
        }
        for (int i = 18; i < 23; i++) {
            Exercise exercise = new Exercise(Style.BREASTSTROKE);
            exercise.setName(Style.BREASTSTROKE.name() + i);
            exercise.setDescription(description + i);
            exercise.setWarmUpRelax(true);
            saveExercise(exercise);
        }
    }

    private void saveExercise(Exercise exercise) {
        exerciseRepository.saveAndFlush(exercise);
    }

    private StringBuilder addParamsToUrl(StringBuilder url, Long trainingId,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate) {
        if (trainingId != null) {
            url.append("trainingId=" + trainingId.toString() + "&");
        }
        if (startDate != null) {
            url.append("startDate=" + startDate.toString() + "&");
        }
        if (endDate != null) {
            url.append("endDate=" + endDate.toString());
        }
        return url;
    }
}
