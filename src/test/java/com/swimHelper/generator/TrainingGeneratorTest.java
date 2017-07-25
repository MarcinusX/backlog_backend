package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class TrainingGeneratorTest {

    private final ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
    private final Random random = new Random();
    private final TrainingGenerator sut = new TrainingGenerator(exerciseRepository, random);

    @Test
    public void generateTraining_whenValidParameters_doesntThrowException() throws Exception {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user chooses training style he has to have style statistics
    @Test
    public void generateTraining_whenMissingUserData_shouldThrowException() {
        //given
        User user = createValidUser();
        user.setStyleStatistics(new ArrayList<>());
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose at least one style
    @Test
    public void generateTraining_whenMissingStyles_shouldThrowException() {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setStyles(new ArrayList<>());
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose difficulty level
    @Test
    public void generateTraining_whenMissingDifficultyLevel_shouldThrowException() {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setDifficultyLevel(null);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose intensity level
    @Test
    public void generateTraining_whenMissingIntensityLevel_shouldThrowException() {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(null);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDurationAndMaxDistance_shouldThrowException() {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(0);
        trainingRequirements.setMaxDurationInMinutes(0);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDistance_shouldNotThrowException() throws Exception {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(0);
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDuration_shouldNotThrowException() throws Exception {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInMinutes(0);
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //check if training is not null
    @Test
    public void generateTraining_whenValidParameters_doesntReturnNull() throws Exception {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training).isNotNull();
    }

    //when user generates training the most important are given styles
    @Test
    public void generateTraining_whenStylesGiven_shouldReturnTrainingInGivenStyles() throws MissingTrainingRequirementsException {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        List<Style> stylesUsed = training.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        assertThat(stylesUsed).containsExactlyElementsOf(trainingRequirements.getStyles());
    }

    //when user generates training exercise series is created on the basis of intensity level
    @Test
    public void generateTraining_whenIntensityLevelGiven_shouldCreateExerciseSeriesDueToIntensityLevel() throws Exception {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //then
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        List<Integer> exerciseSeriesDistances = training.getExerciseSeries()
                .stream()
                .map(ExerciseSeries::getDistance)
                .collect(Collectors.toList());
        boolean areDistancesCorrect = exerciseSeriesDistances.stream().allMatch(distance -> IntensityLevel.LOW.getDistances().contains(distance));
        assertThat(areDistancesCorrect).isTrue();
    }

    @Test
    public void generateTraining_whenMaxDurationGiven_shouldReturnTrainingWithGivenMaxDuration() throws MissingTrainingRequirementsException {
        //given
        User user = createValidUser();
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));        //when
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training.getDurationInMinutes()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInMinutes());
    }

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(2);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInMinutes(40);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(3);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInMinutes(60);
        List<Integer> possibleNumberOfSeries = new ArrayList<>();
        possibleNumberOfSeries.add(3);
        possibleNumberOfSeries.add(4);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isIn(possibleNumberOfSeries);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInMinutes(50);
        List<Integer> numberOfExerciseSeriesForHighLevel = new ArrayList<>();
        numberOfExerciseSeriesForHighLevel.add(5);
        numberOfExerciseSeriesForHighLevel.add(6);
        numberOfExerciseSeriesForHighLevel.add(7);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isIn(numberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInMinutes(40);
        List<Integer> numberOfExerciseSeriesForHighLevel = new ArrayList<>();
        numberOfExerciseSeriesForHighLevel.add(3);
        numberOfExerciseSeriesForHighLevel.add(4);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isIn(numberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenShortMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInMinutes(25);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(3);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDurationGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setMaxDurationInMinutes(10);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(1);
    }

    @Test
    public void getDurationOfOneExerciseSeries_whenNumberOfExerciseSeriesAndMaxDurationGiven_shouldReturnDurationOfOneExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = createValidTrainingRequirements();
        //when
        Integer durationOfOneExerciseSeries = sut.getDurationOfOneExerciseSeries(8, trainingRequirements.getMaxDurationInMinutes());
        //then
        assertThat(durationOfOneExerciseSeries).isEqualTo(3);
    }


    private TrainingRequirements createValidTrainingRequirements() {
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.FREESTYLE);
        styles.add(Style.BACKSTROKE);
        Collection<Equipment> availableEquipment = new ArrayList<>();
        availableEquipment.add(Equipment.TEMP);
        Collection<TrainingPurpose> trainingPurposes = new ArrayList<>();
        trainingPurposes.add(TrainingPurpose.IMPROVE_RECORDS);
        return new TrainingRequirements(styles, DifficultyLevel.BEGINNER, IntensityLevel.LOW, 30, 1000,
                availableEquipment, trainingPurposes);
    }

    private User createValidUser() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(1L, Style.BACKSTROKE, 100, 120));
        styleStatistics.add(new StyleStatistics(2L, Style.FREESTYLE, 100, 100));
        styleStatistics.add(new StyleStatistics(3L, Style.BREASTSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(4L, Style.BUTTERFLY, 100, 230));
        styleStatistics.add(new StyleStatistics(5L, Style.INDIVIDUAL_MEDLEY, 100, 140));

        User user = new User();
        user.setStyleStatistics(styleStatistics);
        return user;
    }
}