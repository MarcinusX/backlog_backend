package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class UserNotSignedToCompetition extends BusinessException {
    public UserNotSignedToCompetition() {
        super();
    }

    public UserNotSignedToCompetition(String message) {
        super(message);
    }

    public UserNotSignedToCompetition(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotSignedToCompetition(Throwable cause) {
        super(cause);
    }

    protected UserNotSignedToCompetition(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
