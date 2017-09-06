package com.swimHelper.component.calories;

import com.swimHelper.model.Style;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@Component
public class CaloryBurnFactorFactory {

    private final HashMap<Style, Double> METFactors;
    /**
     * Unit: meter/second
     */
    private final HashMap<Style, Double> baseSpeeds;

    /*
        https://www.hss.edu/conditions_burning-calories-with-exercise-calculating-estimated-energy-expenditure.asp
     */
    public CaloryBurnFactorFactory() {
        METFactors = new HashMap<>();
        METFactors.put(Style.BACKSTROKE, 8.0);
        METFactors.put(Style.FREESTYLE, 9.0);
        METFactors.put(Style.BREASTSTROKE, 10.0);
        METFactors.put(Style.BUTTERFLY, 11.0);
        baseSpeeds = new HashMap<>();
        baseSpeeds.put(Style.BACKSTROKE, 0.74); //2:15min / 100 m
        baseSpeeds.put(Style.FREESTYLE, 0.83); //2:00min / 100 m
        baseSpeeds.put(Style.BREASTSTROKE, 0.63); //2:40min / 100 m
        baseSpeeds.put(Style.BUTTERFLY, 0.67); //2:30min / 100 m
    }

    public double getFactor(Style style) {
        return METFactors.get(style);
    }

    public double getBaseSpeed(Style style) {
        return baseSpeeds.get(style);
    }
}
