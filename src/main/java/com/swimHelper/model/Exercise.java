package com.swimHelper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    private String name;
    private String description;
    private String videoUrl;
    private Style style;
    private boolean isWarmUpRelax = false;
    public Exercise(Style style) {
        this.style = style;
    }
}
