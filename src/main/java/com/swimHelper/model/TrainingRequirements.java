package com.swimHelper.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Data
public class TrainingRequirements {
    private Collection<Style> styles = new ArrayList<>();
    private DifficultyLevel difficultyLevel;
    private int maxDurationInMinutes;
    private int maxDistance;
    private Collection<Equipment> availableEquipment = new ArrayList<>();
    private Collection<TrainingPurpose> trainingPurposes = new ArrayList<>();
}
