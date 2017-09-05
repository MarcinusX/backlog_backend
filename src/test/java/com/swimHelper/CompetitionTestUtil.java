package com.swimHelper;

import com.swimHelper.model.Competition;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
@Component
public class CompetitionTestUtil {

    public Competition createValidCompetition() {
        Competition competition = new Competition();
        competition.setDateTime(LocalDateTime.now().plusDays(1));
        competition.setLocation(new Point(123, 84));
        competition.setLocationName("Some name of location");
        competition.setMaxParticipants(50);
        competition.setName("naame");
        return competition;
    }
}
