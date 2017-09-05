package com.swimHelper.service.training;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.exception.*;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.TrainingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
    private ExerciseSeriesRepository exerciseSeriesRepository;

    @Autowired
    private TestUtil testUtil;

    @After
    public void cleanUp() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
    }

    @Before
    public void prepare() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        boolean areStylesCorrect = stylesInGeneratedTraining.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(areStylesCorrect).isTrue();
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        boolean areStylesCorrect = stylesInGeneratedTraining.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(training.getUser()).isEqualTo(savedUser);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(areStylesCorrect).isTrue();
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        trainingRequirements.setTrainingDateTime(nextMonday);
        //when
        Training training = sut.generateTraining(trainingRequirements, savedUser.getId());
        //then
        assertThat(training.getExerciseSeries().size()).isGreaterThan(2);
    }

    @Test
    public void countDistance_whenUser_shouldReturnDistanceOfAllTrainingsForUser() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser);
        //when
        int distance = sut.countDistance(savedUser.getId(), null, null, null);
        //given
        assertThat(distance).isGreaterThan(0);
    }

    @Test
    public void countDistance_whenUserAndDates_shouldReturnDistanceTrainingsBetweenDates() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser); //only one training is between given dates
        //when
        int distance = sut.countDistance(savedUser.getId(), null, LocalDateTime.of(2017, 1, 1, 6, 40, 45), LocalDateTime.of(2017, 1, 30, 6, 40, 45));
        //given
        assertThat(distance).isEqualTo(4500);
    }

    private void addExercisesInSpecifiedStyle(Style style) {
        String description = "description";
        for (int i = 0; i < 12; i++) {
            Exercise exercise = new Exercise(style);
            exercise.setName(style.name() + i);
            exercise.setDescription(description + i);
            saveExercise(exercise);
        }
    }

    private void addTrainings(User user) {
        for (int i = 1; i < 6; i++) {
            Training training = testUtil.createValidTraining();
            training.setTrainingDateTime(LocalDateTime.of(2017, i, 10, 6, 40, 45));
            training.getExerciseSeries().forEach(es -> es.setCompletedRepeats(3));
            training.setUser(user);
            trainingRepository.saveAndFlush(training);
        }
    }

    private void addWarmUpRelaxExercises() {
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
}