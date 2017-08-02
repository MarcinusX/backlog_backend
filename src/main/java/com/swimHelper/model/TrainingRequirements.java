package com.swimHelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor //jpa
public class TrainingRequirements {
    private Collection<Style> styles = new ArrayList<>();
    private DifficultyLevel difficultyLevel;
    private IntensityLevel intensityLevel;
    private int maxDurationInSeconds;
    private int maxDistance;
    private Collection<Equipment> availableEquipment = new ArrayList<>();
    private Collection<TrainingPurpose> trainingPurposes = new ArrayList<>();
}
