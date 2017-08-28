package com.swimHelper.exception;

import com.swimHelper.model.NotificationMedium;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class UnsupportedNotificationMediumException extends BusinessException {
    public UnsupportedNotificationMediumException(NotificationMedium medium) {
        super("Medium " + medium + " is not supported yet.");
    }
}
