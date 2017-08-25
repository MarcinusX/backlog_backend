package com.swimHelper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
@EqualsAndHashCode(exclude = "exerciseSeries")
public class Training {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    private Collection<ExerciseSeries> exerciseSeries = new ArrayList<>();
    private LocalDateTime dateTime;
    private int durationInMinutes;
    @ManyToOne
    private User user;
    private boolean hasUserBeenNotified;
    private LocalDateTime notificationDateTime;
}
