package com.swimHelper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(unique = true)
    private String name;
    private String description;
    private String videoUrl;
    @NotNull
    private Style style;
    private boolean warmUpRelax;
    @ElementCollection(targetClass = TrainingEquipment.class)
    @JoinTable(name = "training_equipment", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "equipment", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<TrainingEquipment> requiredTrainingEquipment = new ArrayList<>();
    public Exercise(Style style) {
        this.style = style;
    }
}
