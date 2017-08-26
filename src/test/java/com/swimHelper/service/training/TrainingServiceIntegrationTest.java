package com.swimHelper.service.training;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.generator.TrainingGenerator;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.TrainingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TrainingServiceIntegrationTest {

    @Autowired
    private TrainingService sut;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingGenerator trainingGenerator;

    @Autowired
    private TestUtil testUtil;

    @Before
    public void prepareDatabaseForTests() {
        trainingRepository.deleteAll();
        userRepository.deleteAll();
        addExercisesInSpecifiedStyle(Style.BACKSTROKE);
        addExercisesInSpecifiedStyle(Style.FREESTYLE);
        addExercisesInSpecifiedStyle(Style.BREASTSTROKE);
        addExercisesInSpecifiedStyle(Style.BUTTERFLY);
        addWarmUpRelaxExercises();
    }

    @Test
    public void generateTraining_whenMissingTrainingRequirements_shouldThrowException() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.LOW);
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.BUTTERFLY);
        trainingRequirements.setStyles(styles);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(trainingRequirements, savedUser.getId()));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    @Test
    public void generateTraining_whenMissingUserStatistics_shouldThrowException() throws BusinessException {
        //given
        User user = new User();
        user.setEmail("email");
        user.setPassword("password");
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.LOW);
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.BUTTERFLY);
        trainingRequirements.setStyles(styles);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(trainingRequirements, savedUser.getId()));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    @Test
    public void generateTraining_whenButterflyStyleAndLowIntensityAndLongMaxDuration_shouldGenerateCorrectlyTraining() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(5000);
        trainingRequirements.setIntensityLevel(IntensityLevel.LOW);
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.BUTTERFLY);
        trainingRequirements.setStyles(styles);
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        //then
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(stylesInGeneratedTraining).containsOnly(Style.BUTTERFLY);
    }

    @Test
    public void generateTraining_whenTwoStylesAndHighIntensityAndAverageMaxDuration_shouldGenerateCorrectlyTraining() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3000);
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setStyles(Arrays.asList(Style.BACKSTROKE, Style.FREESTYLE));
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        //then
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(stylesInGeneratedTraining).containsOnly(Style.FREESTYLE, Style.BACKSTROKE);
    }

    @Test
    public void generateTraining_whenThreeStylesAndMediumIntensityAndAverageMaxDuration_shouldGenerateCorrectlyTraining() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3000);
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setStyles(Arrays.asList(Style.BACKSTROKE, Style.FREESTYLE, Style.BREASTSTROKE));
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        //then
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(stylesInGeneratedTraining).containsOnly(Style.FREESTYLE, Style.BACKSTROKE, Style.BREASTSTROKE);
    }

    @Test
    public void generateTraining_whenShortMaxDuration_shouldGenerateCorrectlyTrainingOnlyWithWarmupAndRelax() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(900);
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setStyles(Arrays.asList(Style.BACKSTROKE, Style.FREESTYLE));
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getExerciseSeries().size()).isEqualTo(2);
    }

    @Test
    public void generateTraining_whenTooShortMaxDuration_shouldThrowException() throws BusinessException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.LOW);
        trainingRequirements.setMaxDurationInSeconds(600);
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.BUTTERFLY);
        trainingRequirements.setStyles(styles);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(trainingRequirements, savedUser.getId()));
        //then
        assertThat(throwable).isInstanceOf(UnsatisfiedTimeRequirementsException.class);
    }

    @Test
    public void generateTraining_whenUserWithWeakStatistics_shouldGenerateCorrectlyTraining() throws BusinessException {
        //given
        User user = testUtil.createValidUserWithWeakStatistics();
        User savedUser = userRepository.saveAndFlush(user);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setMaxDurationInSeconds(3000);
        trainingRequirements.setStyles(Arrays.asList(Style.BACKSTROKE, Style.FREESTYLE));
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        assertThat(training.getExerciseSeries().size()).isGreaterThan(2);
    }

    private void addExercisesInSpecifiedStyle(Style style) {
        String name = "name";
        String description = "description";
        for (int i = 0; i < 12; i++) {
            Exercise exercise = new Exercise(style);
            exercise.setName(name + i);
            exercise.setDescription(description + i);
            saveExercise(exercise);
        }
    }

    private void addWarmUpRelaxExercises() {
        String name = "name";
        String description = "description";
        for (int i = 13; i < 17; i++) {
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setName(name + i);
            exercise.setDescription(description + i);
            exercise.setWarmUpRelax(true);
            saveExercise(exercise);
        }
        for (int i = 18; i < 23; i++) {
            Exercise exercise = new Exercise(Style.BREASTSTROKE);
            exercise.setName(name + i);
            exercise.setDescription(description + i);
            exercise.setWarmUpRelax(true);
            saveExercise(exercise);
        }
    }

    private void saveExercise(Exercise exercise) {
        exerciseRepository.saveAndFlush(exercise);
    }
}