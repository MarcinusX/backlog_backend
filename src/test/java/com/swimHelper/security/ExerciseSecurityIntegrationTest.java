package com.swimHelper.security;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.model.Exercise;
import com.swimHelper.model.Style;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mstobieniecka on 2017-08-28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("security")
public class ExerciseSecurityIntegrationTest {

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
    public void addExercise_whenUserAuthorized_returns403() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        testUtil.addUser(testRestTemplate);
        //when
//        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void addExercise_whenAdminAuthorized_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        testUtil.createAdminForTests();
        //when
//        ResponseEntity<Exercise> responseEntity = trainingTestUtil.postExercise(testRestTemplate, exercise, TrainingTestUtil.ADMIN_EMAIL, TrainingTestUtil.ADMIN_PASSWORD);
        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void updateExercise_whenAdminAuthorized_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        savedExercise.setName("name1");
        testUtil.createAdminForTests();
        JwtUser admin = new JwtUser(TrainingTestUtil.ADMIN_EMAIL, TrainingTestUtil.ADMIN_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, admin);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, savedExercise, authorizationHeader);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateExercise_whenUserAuthorized_returns403() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        exerciseRepository.saveAndFlush(exercise);
        Exercise exercise1 = new Exercise(Style.BACKSTROKE);
        exercise1.setName("name1");
        testUtil.addUser(testRestTemplate);
        JwtUser admin = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, admin);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.putExercise(testRestTemplate, exercise1, authorizationHeader);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getExercise_whenUserAuthorized_returns403() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        testUtil.addUser(testRestTemplate);
        JwtUser admin = new JwtUser(TrainingTestUtil.USER_EMAIL, TrainingTestUtil.USER_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, admin);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.getExercise(testRestTemplate, savedExercise.getId(),
                authorizationHeader);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getExercise_whenAdminAuthorized_returns200() throws Exception {
        //given
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setName("name");
        exercise.setDescription("description");
        Exercise savedExercise = exerciseRepository.saveAndFlush(exercise);
        testUtil.createAdminForTests();
        JwtUser admin = new JwtUser(TrainingTestUtil.ADMIN_EMAIL, TrainingTestUtil.ADMIN_PASSWORD);
        String authorizationHeader = trainingTestUtil.getAuthorizationHeader(testRestTemplate, admin);
        //when
        ResponseEntity<Exercise> responseEntity = trainingTestUtil.getExercise(testRestTemplate, savedExercise.getId(), authorizationHeader);
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
