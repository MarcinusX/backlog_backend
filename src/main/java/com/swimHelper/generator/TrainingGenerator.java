package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Component
public class TrainingGenerator {
    public Training generateTraining(User user, TrainingRequirements trainingRequirements) throws MissingTrainingRequirementsException {
        if (!areTrainingRequirementsGiven(user, trainingRequirements)) {
            throw new MissingTrainingRequirementsException();
        }
        return null;
    }

    private boolean areTrainingRequirementsGiven(User user, TrainingRequirements trainingRequirements) {
        Collection<Style> userStylesFromStatistics =
                user.getStyleStatistics().stream().map(StyleStatistics::getStyle).collect(Collectors.toList());
        boolean doesUserHaveStatisticsForChosenStyles = trainingRequirements.getStyles().stream().allMatch(userStylesFromStatistics::contains);
        boolean doesUserChoseStyles = !trainingRequirements.getStyles().isEmpty();
        return (doesUserHaveStatisticsForChosenStyles && doesUserChoseStyles);
    }
}
