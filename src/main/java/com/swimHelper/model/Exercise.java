package com.swimHelper.model;

import javax.persistence.Entity;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
public class Exercise {
    private String name;
    private DifficultyLevel difficultyLevel;
    private String description;
    private String videoUrl;
    private Collection<Equipment> requiredEquipment;
    private Style style;
    private Collection<TrainingPurpose> purposes;
}
