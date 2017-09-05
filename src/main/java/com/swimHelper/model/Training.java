package com.swimHelper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
@ToString(exclude = {"user", "exerciseSeries"})
@EqualsAndHashCode(exclude = "exerciseSeries")
public class Training {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.PERSIST)
    private Collection<ExerciseSeries> exerciseSeries = new ArrayList<>();
    private LocalDateTime dateTime;
    private int durationInSeconds;
    @ManyToOne
    @JsonIgnoreProperties(value = {"trainings"}, allowSetters = true)
    private User user;
    private boolean hasUserBeenNotified;
    private LocalDateTime notificationDateTime;
    private LocalDateTime trainingDateTime;
}
