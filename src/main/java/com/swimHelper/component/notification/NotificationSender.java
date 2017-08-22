package com.swimHelper.component.notification;

import com.swimHelper.model.Training;
import com.swimHelper.model.User;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public interface NotificationSender {
    void remindTraining(User user, Training training);
}
