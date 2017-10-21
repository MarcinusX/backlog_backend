package com.swimHelper.model;

/**
 * Created by mstobieniecka on 2017-10-17.
 */
public enum TrainingEquipment {
    KICKBOARD("Kickboard"),
    PULL_BUOYS("Pull buoy"),
    PADDLES("Swim Paddles"),
    FINS("Swim Fins"),
    SNORKEL("Swim Snorkel");

    private String name;

    TrainingEquipment(String name) {
        this.name = name;
    }
}
