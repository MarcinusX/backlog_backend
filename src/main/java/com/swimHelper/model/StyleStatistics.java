package com.swimHelper.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class StyleStatistics {
    @Id
    @GeneratedValue
    private Long id;
    private Style style;
    private int distance;
    private int timeInSeconds;
}
