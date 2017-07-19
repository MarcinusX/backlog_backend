package com.swimHelper.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Exercise {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private DifficultyLevel difficultyLevel;
    private String description;
    private String videoUrl;
    private Style style;
    @ElementCollection(targetClass = Equipment.class)
    @Enumerated
    private Collection<Equipment> requiredEquipment;
    @ElementCollection(targetClass = TrainingPurpose.class)
    @Enumerated
    private Collection<TrainingPurpose> purposes;
}
