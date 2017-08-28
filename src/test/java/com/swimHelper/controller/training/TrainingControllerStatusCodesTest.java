package com.swimHelper.controller.training;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
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
    public void generateTraining_returnsOK() throws Exception {
        //given
        trainingTestUtil.addUser(testRestTemplate);
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
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
        trainingTestUtil.addUser(testRestTemplate);
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
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
        trainingTestUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(600);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
