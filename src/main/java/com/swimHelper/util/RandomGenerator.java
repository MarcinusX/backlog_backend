package com.swimHelper.util;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Created by mstobieniecka on 2017-08-21.
 */
@Component
public class RandomGenerator {

    public int generateRandomIntFromRange(int min, int max) {
        Random random = new Random();
        return random.ints(min, (max + 1)).findFirst().getAsInt();
    }

    public int generateRandomInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}
