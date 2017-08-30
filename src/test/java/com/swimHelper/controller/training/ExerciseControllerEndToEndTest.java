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
    private TrainingTestUtil trainingTestUtil;

    @Before
    public void prepare() {
        exerciseRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        exerciseRepository.deleteAll();
    }

    @Test
    public void addExerciseTest() throws Exception {
        //given
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, null, null);
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
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        ResponseEntity<Exercise> savedResponseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, null, null);
        Exercise savedExercise = savedResponseEntity.getBody();
        savedExercise.setDescription("description1");
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, savedExercise, null, null);
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
        Exercise exercise = new Exercise();
        exercise.setDescription("description");
        exercise.setName("name");
        exercise.setStyle(Style.BACKSTROKE);
        ResponseEntity<Exercise> savedResponseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, null, null);
        Exercise savedExercise = savedResponseEntity.getBody();
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.getExercise(testRestTemplate, savedExercise.getId(), null, null);
        Exercise exerciseFromResponse = responseEntity.getBody();
        //then
        assertThat(exerciseFromResponse.getName()).isEqualTo("name");
        assertThat(exerciseFromResponse.getDescription()).isEqualTo("description");
        assertThat(exerciseFromResponse.getStyle()).isEqualTo(Style.BACKSTROKE);
        assertThat(exerciseFromResponse.getId()).isNotNull();
    }
}
