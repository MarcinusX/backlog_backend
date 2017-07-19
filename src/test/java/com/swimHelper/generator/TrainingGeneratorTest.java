package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class TrainingGeneratorTest {

    private final TrainingGenerator sut = new TrainingGenerator();

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

    private TrainingRequirements createValidTrainingRequirements() {
        Collection<Style> styles = new ArrayList<>();
        styles.add(Style.FREESTYLE);
        styles.add(Style.BACKSTROKE);
        Collection<Equipment> availableEquipment = new ArrayList<>();
        availableEquipment.add(new Equipment());
        Collection<TrainingPurpose> trainingPurposes = new ArrayList<>();
        trainingPurposes.add(TrainingPurpose.IMPROVE_RECORDS);
        return new TrainingRequirements(styles, DifficultyLevel.BEGINNER, 30, 1000,
                availableEquipment, trainingPurposes);
    }

    private User createValidUser() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 120));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 100));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 230));
        styleStatistics.add(new StyleStatistics(Style.INDIVIDUAL_MEDLEY, 100, 140));

        User user = new User();
        user.setStyleStatistics(styleStatistics);
        return user;
    }
}