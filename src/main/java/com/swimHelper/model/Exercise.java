package com.swimHelper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

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
    public Exercise(Style style) {
        this.style = style;
    }
}
