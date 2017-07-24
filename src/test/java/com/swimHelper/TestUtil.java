package com.swimHelper;

import com.swimHelper.model.Style;
import com.swimHelper.model.StyleStatistics;
import com.swimHelper.model.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
public class TestUtil {

    public static User createValidUser() {
        Collection<StyleStatistics> styleStatistics = new ArrayList<>();
        styleStatistics.add(new StyleStatistics(Style.BACKSTROKE, 100, 120));
        styleStatistics.add(new StyleStatistics(Style.FREESTYLE, 100, 100));
        styleStatistics.add(new StyleStatistics(Style.BREASTSTROKE, 100, 200));
        styleStatistics.add(new StyleStatistics(Style.BUTTERFLY, 100, 230));
        styleStatistics.add(new StyleStatistics(Style.INDIVIDUAL_MEDLEY, 100, 140));

        User user = new User();
        user.setStyleStatistics(styleStatistics);
        user.setEmail("some@email.com");
        user.setPassword("somePassword");
        return user;
    }
}
