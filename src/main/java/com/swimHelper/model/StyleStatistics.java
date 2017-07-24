package com.swimHelper.model;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor //jpa
public class StyleStatistics {
    @Id
    @GeneratedValue
    private Long id;
    private Style style;
    private int distance;
    private int timeInSeconds;

    public StyleStatistics(Style style, int distance, int timeInSeconds) {
        this.style = style;
        this.distance = distance;
        this.timeInSeconds = timeInSeconds;
    }
}
