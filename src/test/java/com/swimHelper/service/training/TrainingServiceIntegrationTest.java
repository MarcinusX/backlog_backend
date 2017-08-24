package com.swimHelper.service.training;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.BusinessException;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
        addExercisesInSpecifiedStyle(Style.BACKSTROKE);
        addExercisesInSpecifiedStyle(Style.FREESTYLE);
        addExercisesInSpecifiedStyle(Style.BREASTSTROKE);
        addExercisesInSpecifiedStyle(Style.INDIVIDUAL_MEDLEY);
        addExercisesInSpecifiedStyle(Style.BUTTERFLY);
        addWarmUpRelaxExercises();
        addUsers();
    }

    @Test
    public void generateTraining_whenButterflyStyleAndLowIntensityAndShortMaxDuration_shouldGenerateCorrectlyTraining() throws BusinessException {
        //given
        User user = userRepository.findOne(1L);
        TrainingRequirements trainingRequirements = new TrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(4000);
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.BUTTERFLY);
        trainingRequirements.setStyles(styles);
        //when
        Training training = sut.generateTraining(trainingRequirements, user.getId());
        List<Style> stylesInGeneratedTraining = training.getExerciseSeries().
                stream().filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        //then
        boolean equals = user.equals(training.getUser());
        assertThat(training.getUser()).isEqualTo(user);
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
        assertThat(stylesInGeneratedTraining).containsOnly(Style.BUTTERFLY);
    }

    private void addUsers() {
        String email = "email@example.com";
        for (int i = 0; i < 5; i++) {
            User user = testUtil.createValidUser();
            user.setId((long) i);
            user.setEmail(i + email);
            userRepository.saveAndFlush(user);
        }
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