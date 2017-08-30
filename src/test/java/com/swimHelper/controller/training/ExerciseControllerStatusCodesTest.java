package com.swimHelper.controller.training;

import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.Exercise;
import com.swimHelper.model.Style;
import com.swimHelper.repository.ExerciseRepository;
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
@ActiveProfiles("test")
public class ExerciseControllerStatusCodesTest {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private TrainingTestUtil trainingTestUtil;

    @Before
    public void before() {
        exerciseRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        exerciseRepository.deleteAll();
    }

    @Test
    public void getExercise_whenExerciseExists_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise addedExercise = exerciseRepository.saveAndFlush(exercise);
        //when
        ResponseEntity<Exercise> responseEntity =
                testRestTemplate.getForEntity("/exercises/" + addedExercise.getId(), Exercise.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getExercise_whenExerciseMissing_returns404() throws Exception {
        //given empty repo
        //when
        ResponseEntity<Exercise> responseEntity = testRestTemplate.getForEntity("/exercises/1", Exercise.class);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void addExercise_whenExerciseValid_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void addExercise_whenExerciseWithoutStyle_returns400() throws Exception {
        //given
        Exercise exercise = new Exercise();
        exercise.setName("name");
        exercise.setDescription("description");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addExercise_whenExerciseWithExistingName_returns409() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        exerciseRepository.saveAndFlush(exercise);
        Exercise exerciseWithExistingName = new Exercise(Style.BACKSTROKE);
        exerciseWithExistingName.setName("name");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exerciseWithExistingName, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void updateExercise_whenValidExercise_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        savedExercise.setName("name1");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, savedExercise, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateExercise_whenExerciseWithoutStyle_returns400() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        savedExercise.setStyle(null);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, savedExercise, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateExercise_whenExerciseWithExistingName_returns409() throws Exception {
        //given
        Exercise exercise1 = new Exercise(Style.BACKSTROKE);
        exercise1.setName("name");
        exercise1.setDescription("description");
        exerciseRepository.saveAndFlush(exercise1);
        Exercise exercise2 = new Exercise(Style.BACKSTROKE);
        exercise2.setName("name1");
        exercise2.setDescription("description");
        Exercise savedExercise2 = exerciseRepository.saveAndFlush(exercise2);
        savedExercise2.setName("name");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, savedExercise2, null, null);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
