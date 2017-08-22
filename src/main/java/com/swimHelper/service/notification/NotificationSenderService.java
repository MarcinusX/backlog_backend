package com.swimHelper.service.notification;

import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import org.springframework.stereotype.Service;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Service
public interface NotificationSenderService {
    void remindTraining(User user, Training training);
}
