package com.swimHelper.controller.calories;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.IntegerWrapper;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
import com.swimHelper.model.User;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-09-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class CaloriesControllerStatusCodesTest {
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
    public void calculateCalories_whenNoParameters_returns200() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        User user = testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate, null, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void calculateCalories_whenStartDateAndEndDate_returns200() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        User user = testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements);
        LocalDateTime startDate = LocalDateTime.of(2017, 7, 1, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2017, 8, 30, 6, 40, 45);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate, null, startDate, endDate);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void calculateCalories_whenTrainingId_returns200() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        User user = testUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        Training training = trainingTestUtil.addTraining(testRestTemplate, trainingRequirements);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate, training.getId(), null, null);
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
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate, 1L, startDate, endDate);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
