package com.swimHelper;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.security.JwtUser;
import com.swimHelper.util.JsonUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.swimHelper.security.SecurityConstants.HEADER_STRING;

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

    public ResponseEntity<Exercise> postExercise(TestRestTemplate testRestTemplate, Exercise exercise, String authorizationHeader) throws IOException, JSONException {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return testRestTemplate.postForEntity("/exercises", entity, Exercise.class);
    }

    public ResponseEntity<Exercise> putExercise(TestRestTemplate testRestTemplate, Exercise exercise, String authorizationHeader) {
        String json = jsonUtil.toJson(exercise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return testRestTemplate.exchange("/exercises", HttpMethod.PUT, entity, Exercise.class);
    }

    public ResponseEntity<Exercise> getExercise(TestRestTemplate testRestTemplate, Long id, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return testRestTemplate.exchange("/exercises/" + id, HttpMethod.GET, entity, Exercise.class);
    }

    public ResponseEntity<Training> postTrainingRequirements(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements, String authorizationHeader) {
        LocalDateTime trainingDateTime = trainingRequirements.getTrainingDateTime();
        String json = jsonUtil.toJson(trainingRequirements);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return testRestTemplate.exchange("/trainings", HttpMethod.POST, entity, Training.class);
    }

    public ResponseEntity<Training> putTraining(TestRestTemplate testRestTemplate, Training training, String authorizationHeader) throws IOException, JSONException {
        String json = jsonUtil.toJson(training);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.exchange("/trainings", HttpMethod.PUT, entity, Training.class);
    }

    public ResponseEntity<IntegerWrapper> countDistance(TestRestTemplate testRestTemplate,
                                                        Long trainingId,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        String authorizationHeader) {
        StringBuilder url = new StringBuilder().append("/trainings?");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set(HEADER_STRING, authorizationHeader);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        url = addParamsToUrl(url, trainingId, startDate, endDate);

        return testRestTemplate.exchange(url.toString(), HttpMethod.GET, entity, IntegerWrapper.class);
    }

    public ResponseEntity<IntegerWrapper> calculateCalories(TestRestTemplate testRestTemplate,
                                                            Long trainingId,
                                                            LocalDate startDate,
                                                            LocalDate endDate,
                                                            String authorizationHeader) throws IOException, JSONException {
        StringBuilder url = new StringBuilder().append("/calories?");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set(HEADER_STRING, authorizationHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        url = addParamsToUrl(url, trainingId, startDate, endDate);

        return testRestTemplate.exchange(url.toString(), HttpMethod.GET, entity, IntegerWrapper.class);
    }

    //*****************TRAININGS******************//

    public void addTrainings(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements, String authorizationHeader) throws IOException, JSONException {
        for (int i = 0; i < 5; i++) {
            addTraining(testRestTemplate, trainingRequirements, authorizationHeader);
        }
    }

    public Training addTraining(TestRestTemplate testRestTemplate, TrainingRequirements trainingRequirements, String authorizationHeader) throws IOException, JSONException {
        ResponseEntity<Training> trainingResponseEntity = postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training training = trainingResponseEntity.getBody();
        training.getExerciseSeries().forEach(es -> {
            es.setCompletedRepeats(3);
            es.setAverageDurationOfOneRepeatInSeconds(300);
        });
        return putTraining(testRestTemplate, training, authorizationHeader).getBody();
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

    public void addExercises(TestRestTemplate testRestTemplate, String authorizationHeader) throws IOException, JSONException {
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

            ResponseEntity<Exercise> responseEntity = postExercise(testRestTemplate, exercise, authorizationHeader);
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
                                         LocalDate startDate,
                                         LocalDate endDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (trainingId != null) {
            url.append("trainingId=" + trainingId.toString() + "&");
        }
        if (startDate != null) {
            url.append("startDate=" + startDate.format(dateTimeFormatter)+ "&");
        }
        if (endDate != null) {
            url.append("endDate=" + endDate.format(dateTimeFormatter));
        }
        return url;
    }

    public String getAuthorizationHeader(TestRestTemplate testRestTemplate, JwtUser user) {
        String json = jsonUtil.toJson(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<Object> responseEntity = testRestTemplate.postForEntity("/login", entity, Object.class);
        return responseEntity.getHeaders().get(HEADER_STRING).get(0); //return HEADER_STRING header with token
    }

    public void addExercisesByAdmin(TestRestTemplate testRestTemplate) throws BusinessException, IOException, JSONException {
        JwtUser admin = new JwtUser(TrainingTestUtil.ADMIN_EMAIL, TrainingTestUtil.ADMIN_PASSWORD);
        String authorizationHeader = getAuthorizationHeader(testRestTemplate, admin);
        addExercises(testRestTemplate, authorizationHeader);
    }
}
