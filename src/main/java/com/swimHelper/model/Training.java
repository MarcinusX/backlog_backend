package com.swimHelper.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Training {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.PERSIST)
    private Collection<ExerciseSeries> exerciseSeries = new ArrayList<>();
    private LocalDateTime dateTime;
    private int durationInSeconds;
    @ManyToOne
    private User user;
}
