package com.swimHelper.controller.calories;

import com.swimHelper.*;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.security.JwtUser;
import com.swimHelper.util.JsonUtil;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-09-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class CaloriesControllerEndToEndTest {
    @Autowired
    private JsonUtil jsonUtil;
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
    public void prepare() throws BusinessException {
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
    public void calculateCaloriesTest_shouldReturnBurnedCaloriesOfAllTrainingsForUser() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        trainingTestUtil.addTrainings(testRestTemplate, trainingRequirements, authorizationHeader);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate, null, null, null, authorizationHeader);
        int calculatedCaloriesFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(calculatedCaloriesFromResponse).isGreaterThan(0);
    }

    @Test
    public void calculateCaloriesTest_shouldReturnBurnedCaloriesOfOneTrainingForUser() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addExercisesByAdmin(testRestTemplate);
        testUtil.addUser(testRestTemplate);
        JwtUser user = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, user);
        Training training = trainingTestUtil.addTraining(testRestTemplate, trainingRequirements, authorizationHeader);
        //when
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate,
                training.getId(),
                null,
                null,
                authorizationHeader);
        int distanceFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(distanceFromResponse).isGreaterThan(0);
    }

    @Test
    public void calculateCaloriesTest_shouldReturnBurnedCaloriesOfTrainingsBetweenDatesForUser() throws Exception {
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
        ResponseEntity<IntegerWrapper> responseEntity = trainingTestUtil.calculateCalories(testRestTemplate,
                null,
                startDate,
                endDate,
                authorizationHeader);
        int distanceFromResponse = responseEntity.getBody().getValue();
        //then
        assertThat(distanceFromResponse).isGreaterThan(0);
    }


}
