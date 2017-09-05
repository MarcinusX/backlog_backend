package com.swimHelper.controller.training;

import com.swimHelper.ExerciseSeriesRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class TrainingControllerStatusCodesTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
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
    public void generateTraining_whenValidTrainingRequirements_returns200() throws Exception {
        //given
        testUtil.createAdminForTests(); //required to add exercises
        testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void generateTraining_whenMissingTrainingRequirements_returns400() throws Exception {
        //given
        testUtil.createAdminForTests(); //required to add exercises
        testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(0);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void generateTraining_whenUnsatisfiedTimeRequirements_returns400() throws Exception {
        //given
        testUtil.createAdminForTests(); //required to add exercises
        testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(600);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putTraining_whenInvalidTraining_returns400() throws Exception {
        //given
        testUtil.addUser(testRestTemplate);
        //then
        ResponseEntity<Training> responseEntity = trainingTestUtil.putTraining(testRestTemplate, null);
        //when
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putTraining_whenTrainingDoesntExistTraining_returns404() throws Exception {
        //given
        User user = testUtil.addUser(testRestTemplate);
        Training training = trainingTestUtil.createValidTraining(); //id = 1L
        training.setId(2L);
        training.setUser(user);
        //then
        ResponseEntity<Training> responseEntity = trainingTestUtil.putTraining(testRestTemplate, training);
        //when
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void putTraining_whenValidTraining_returns200() throws Exception {
        //given
        testUtil.createAdminForTests();
        testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        ResponseEntity<Training> responseEntityAddedTraining = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        Training addedTraining = responseEntityAddedTraining.getBody();
        List<ExerciseSeries> exerciseSeriesList = new ArrayList<>(addedTraining.getExerciseSeries());
        ExerciseSeries exerciseSeriesToUpdate = exerciseSeriesList.get(0);
        exerciseSeriesToUpdate.setCompletedRepeats(5);
        exerciseSeriesToUpdate.setAverageDurationOfOneRepeatInSeconds(300);
        //then
        ResponseEntity<Training> responseEntity = trainingTestUtil.putTraining(testRestTemplate, addedTraining);
        //when
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void countDistance_whenNoParameters_returns200() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        User user = testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.countDistance(testRestTemplate, null, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void countDistance_whenTooManyParameters_returns400() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        User user = testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements);
        LocalDateTime startDate = LocalDateTime.of(2017, 7, 1, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2017, 8, 30, 6, 40, 45);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.countDistance(testRestTemplate, 1L, startDate, endDate);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
