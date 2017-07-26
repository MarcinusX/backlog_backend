package com.swimHelper;

import com.swimHelper.model.Style;
import com.swimHelper.model.StyleStatistics;
import com.swimHelper.model.User;
import com.swimHelper.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
@Component
public class TestUtil {

    @Autowired
    JsonUtil jsonUtil;

    public User createValidUser() {
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

    public ResponseEntity<User> postUser(TestRestTemplate testRestTemplate, User user) {
        String json = jsonUtil.toJson(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return testRestTemplate.postForEntity("/users", entity, User.class);
    }
}
