package com.swimHelper.controller.training;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.security.JwtUser;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class TrainingControllerEndToEndTest {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private ExerciseSeriesRepository exerciseSeriesRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TrainingTestUtil trainingTestUtil;

    @Before
    public void prepare() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
    }

    @Test
    public void generateTrainingTest_shouldReturnMoreThanTwoExerciseSeriesNotLongerThan3000Seconds() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        //then
        List<Style> stylesUsed = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        boolean areStylesCorrect = stylesUsed.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(areStylesCorrect).isTrue();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThanOrEqualTo(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(900).isLessThanOrEqualTo(3000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnTrainingOnlyWarmUpExercises() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(900);
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        //then
        List<Boolean> areWarmUpRelaxExercises = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().isWarmUpRelax())
                .collect(Collectors.toList());
        boolean areExercisesOnlyWarmUpRelax = areWarmUpRelaxExercises.contains(false);
        assertThat(areExercisesOnlyWarmUpRelax).isFalse();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isEqualTo(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isEqualTo(900);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnMoreThan0ExerciseSeriesNotLongerThan3000Seconds() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3000);
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        //then
        List<Style> stylesUsed = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        boolean areStylesCorrect = stylesUsed.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(areStylesCorrect).isTrue();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThan(0);
        assertThat(trainingFromResponse.getDurationInSeconds()).isLessThanOrEqualTo(3000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnMoreThan2ExerciseSeriesNotLongerThan5000Seconds() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.getStyles().addAll(Arrays.asList(Style.BREASTSTROKE, Style.BUTTERFLY));
        trainingRequirements.setMaxDurationInSeconds(5000);
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        //then
        List<Style> stylesUsed = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        boolean areStylesCorrect = stylesUsed.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(areStylesCorrect).isTrue();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThan(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(900).isLessThanOrEqualTo(5000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void putTrainingTest_shouldReturnTrainingWithOneExerciseSeriesUpdated() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        ResponseEntity<Training> responseEntityFromAddingTraining = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training addedTraining = responseEntityFromAddingTraining.getBody();
        ExerciseSeries exerciseSeriesToUpdate = new ArrayList<>(addedTraining.getExerciseSeries()).get(0);
        exerciseSeriesToUpdate.setCompletedRepeats(5);
        exerciseSeriesToUpdate.setAverageDurationOfOneRepeatInSeconds(300);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.putTraining(testRestTemplate, addedTraining, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        ExerciseSeries updatedExerciseSeries = trainingFromResponse.getExerciseSeries()
                .stream()
                .filter(ex -> ex.getId().equals(exerciseSeriesToUpdate.getId()))
                .findFirst().get();
        //then
        assertThat(updatedExerciseSeries.getCompletedRepeats()).isEqualTo(exerciseSeriesToUpdate.getCompletedRepeats());
        assertThat(updatedExerciseSeries.getAverageDurationOfOneRepeatInSeconds()).isEqualTo(exerciseSeriesToUpdate.getAverageDurationOfOneRepeatInSeconds());
    }

    @Test
    public void putTrainingTest_shouldReturnTrainingWithEveryExerciseSeriesUpdated() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        ResponseEntity<Training> responseEntityFromAddingTraining = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training addedTraining = responseEntityFromAddingTraining.getBody();
        addedTraining.getExerciseSeries().forEach(es -> {
                    es.setAverageDurationOfOneRepeatInSeconds(5);
                    es.setCompletedRepeats(5);
                }
        );
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.putTraining(testRestTemplate, addedTraining, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        List<ExerciseSeries> exerciseSeriesFromUpdatedTraining = trainingFromResponse.getExerciseSeries().stream().filter(es ->
                es.getAverageDurationOfOneRepeatInSeconds() == 5 && es.getCompletedRepeats() == 5).collect(Collectors.toList());
        //then
        assertThat(exerciseSeriesFromUpdatedTraining.size()).isEqualTo(addedTraining.getExerciseSeries().size());
    }

    @Test
    public void countDistanceTest_shouldReturnDistanceOfAllTrainingsForUser() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements, authorizationHeader);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.countDistance(testRestTemplate, null, null, null,
                                                                                        authorizationHeader);
        int distanceFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(distanceFromResponse).isGreaterThan(0);
    }

    @Test
    public void countDistanceTest_shouldReturnDistanceOfOneTrainingForUser() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        Training training = trainingTestUtil.addTraining(testRestTemplate, trainingRequirements, authorizationHeader);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.countDistance(testRestTemplate,
                training.getId(),
                null,
                null,
                authorizationHeader);
        int distanceFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(distanceFromResponse).isGreaterThan(0);
    }

    @Test
    public void countDistanceTest_shouldReturnDistanceOfTrainingsBetweenDatesForUser() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements, authorizationHeader);
        LocalDateTime startDate = LocalDateTime.of(2100, 7, 30, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2100, 11, 30, 6, 40, 45);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.countDistance(testRestTemplate,
                null,
                startDate,
                endDate,
                authorizationHeader);
        int distanceFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(distanceFromResponse).isGreaterThan(0);
    }

    @Test
    public void generateTrainingTest_shouldReturnTrainingWithExercisesWithEquipment() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements, authorizationHeader);
        Training trainingFromResponse = responseEntity.getBody();
        //then
        List<Boolean> doExercisesContainOnlyAvailableEquimpent = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getRequiredTrainingEquipment().isEmpty()
                        || trainingRequirements.getAvailableTrainingEquipment().containsAll(exerciseSeries1.getExercise()
                        .getRequiredTrainingEquipment()))
                .collect(Collectors.toList());
        boolean areExercisesCorrect = doExercisesContainOnlyAvailableEquimpent.contains(true);
        assertThat(areExercisesCorrect).isTrue();
        assertThat(trainingFromResponse.getId()).isNotNull();
    }
}
