package com.swimHelper.controller.training;

import com.swimHelper.TestUtil;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.UserRepository;
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
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TestUtil testUtil;

    @Test
    public void generateTrainingTest() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        addExercises();
        addUser();
        //when
        ResponseEntity<Training> responseEntity = testUtil.postTrainingRequirements(testRestTemplate, trainingRequirements);
        Training trainingFromResponse = responseEntity.getBody();
        List<Style> stylesFromTraining = trainingFromResponse.getExerciseSeries().stream().
                filter(s -> !s.getExercise().isWarmUpRelax()).map(s -> s.getExercise().getStyle()).collect(Collectors.toList());
        //then
        assertThat(trainingFromResponse.getExerciseSeries().size()).isGreaterThanOrEqualTo(2);
        assertThat(trainingFromResponse.getDurationInSeconds()).isGreaterThan(0);
        assertThat(stylesFromTraining).containsOnly(Style.BACKSTROKE, Style.FREESTYLE);
        assertThat(trainingFromResponse.getId()).isNotNull();
    }

    private void addExercises() {
        exerciseRepository.deleteAll();
        for (int i = 0; i < 6; i++) {
            Exercise exercise = new Exercise(Style.BACKSTROKE);
            exercise.setDescription("desc");
            exercise.setName("name" + i);
            if (i > 2) {
                exercise.setWarmUpRelax(false);
            } else {
                exercise.setWarmUpRelax(true);
            }
            exerciseRepository.saveAndFlush(exercise);
        }
    }

    private void addUser() {
        userRepository.deleteAll();
        User user = testUtil.createValidUser();
        user.setPassword("aaaaa");
        user.setEmail("aaa@example.com");
        userRepository.saveAndFlush(user);
    }
}
