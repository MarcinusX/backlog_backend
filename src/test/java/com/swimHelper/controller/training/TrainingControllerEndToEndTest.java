package com.swimHelper.controller.training;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.IntensityLevel;
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

import java.util.Arrays;
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
    public void generateTrainingTest_shouldReturnMoreThanTwoExerciseSeriesNotLongerThan3000Seconds() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        testUtil.createAdminForTests(); //required to add exercises
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
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(900).isLessThanOrEqualTo(3000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnTrainingOnlyWarmUpExercises() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(900);
        testUtil.createAdminForTests(); //required to add exercises
        trainingTestUtil.addUser(testRestTemplate);
        trainingTestUtil.addExercises(testRestTemplate);
        //when
        ResponseEntity<Training> responseEntity = trainingTestUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        Training trainingFromResponse = responseEntity.getBody();
        List<Boolean> areWarmUpRelaxExercises = trainingFromResponse.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().isWarmUpRelax())
                .collect(Collectors.toList());
        boolean areExercisesOnlyWarmUpRelax = areWarmUpRelaxExercises.contains(false);
        //then
        assertThat(areExercisesOnlyWarmUpRelax).isFalse();
        assertThat(trainingFromResponse.getExerciseSeries().size()).isEqualTo(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isEqualTo(900);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnMoreThan2ExerciseSeriesNotLongerThan2000Seconds() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(2000);
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        testUtil.createAdminForTests(); //required to add exercises
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
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThan(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(900).isLessThanOrEqualTo(2000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    @Test
    public void generateTrainingTest_shouldReturnMoreThan2ExerciseSeriesNotLongerThan5000Seconds() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.getStyles().addAll(Arrays.asList(Style.BREASTSTROKE, Style.BUTTERFLY));
        trainingRequirements.setMaxDurationInSeconds(5000);
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        testUtil.createAdminForTests(); //required to add exercises
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
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThan(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(900).isLessThanOrEqualTo(5000);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }
}
