package com.swimHelper.controller.training;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.Style;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
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
    public void generateTrainingTest() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingTestUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        Training trainingFromResponse = responseEntity.getBody();
        List<Style> stylesUsed = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        boolean areStylesCorrect = stylesUsed.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        //then
        assertThat(areStylesCorrect).isTrue();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThanOrEqualTo(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(0);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }
}
