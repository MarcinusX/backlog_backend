package com.swimHelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Monia on 2017-07-18.
 */
@Data
@AllArgsConstructor
public class StyleStatistics {
    private Style style;
    private int distance;
    private int timeInSeconds;
}
