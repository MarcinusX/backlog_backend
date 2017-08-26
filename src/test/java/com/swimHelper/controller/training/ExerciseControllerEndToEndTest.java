package com.swimHelper.controller.training;

import com.swimHelper.TestUtil;
import com.swimHelper.model.Exercise;
import com.swimHelper.model.Style;
import com.swimHelper.repository.ExerciseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
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
@ActiveProfiles("test")
public class ExerciseControllerEndToEndTest {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TestUtil testUtil;

    @Test
    public void addExerciseTest() throws Exception {
        //given
        exerciseRepository.deleteAll();
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        //when
        ResponseEntity<Exercise> responseEntity = testUtil.postExercise(testRestTemplate, exercise);
        Exercise exerciseFromResponse = responseEntity.getBody();
        //then
        assertThat(exerciseFromResponse.getName()).isEqualTo("name");
        assertThat(exerciseFromResponse.getDescription()).isEqualTo("description");
        assertThat(exerciseFromResponse.getStyle()).isEqualTo(Style.BACKSTROKE);
        assertThat(exerciseFromResponse.getId()).isNotNull();
    }

    @Test
    public void updateExerciseTest() throws Exception {
        //given
        exerciseRepository.deleteAll();
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        savedExercise.setDescription("description1");
        //when
        ResponseEntity<Exercise> responseEntity = testUtil.putExercise(testRestTemplate, savedExercise);
        Exercise exerciseFromResponse = responseEntity.getBody();
        //then
        assertThat(exerciseFromResponse.getName()).isEqualTo("name");
        assertThat(exerciseFromResponse.getDescription()).isEqualTo("description1");
        assertThat(exerciseFromResponse.getStyle()).isEqualTo(Style.BACKSTROKE);
        assertThat(exerciseFromResponse.getId()).isNotNull();
    }

    @Test
    public void getExerciseTest() throws Exception {
        //given
        exerciseRepository.deleteAll();
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        //when
        ResponseEntity<Exercise> responseEntity = testUtil.getExercise(testRestTemplate, savedExercise.getId());
        Exercise exerciseFromResponse = responseEntity.getBody();
        //then
        assertThat(exerciseFromResponse.getName()).isEqualTo("name");
        assertThat(exerciseFromResponse.getDescription()).isEqualTo("description");
        assertThat(exerciseFromResponse.getStyle()).isEqualTo(Style.BACKSTROKE);
        assertThat(exerciseFromResponse.getId()).isNotNull();
    }
}
