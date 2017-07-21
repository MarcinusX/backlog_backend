package com.swimHelper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mstobieniecka on 2017-07-20.
 */
public enum IntensityLevel {
    LOW(300, 400, 500),
    MEDIUM(100, 150, 200),
    HIGH(25, 50, 75);

    private Integer firstDistance;
    private Integer secondDistance;
    private Integer thirdDistance;
    List<Integer> enumValues = new ArrayList<>();

    IntensityLevel(int firstDistance, int secondDistance, int thirdDistance) {
        this.firstDistance = firstDistance;
        this.secondDistance = secondDistance;
        this.thirdDistance = thirdDistance;
        enumValues.add(firstDistance);
        enumValues.add(secondDistance);
        enumValues.add(thirdDistance);
    }

    public List<Integer> getDistances() {
        return enumValues;
    }
}
