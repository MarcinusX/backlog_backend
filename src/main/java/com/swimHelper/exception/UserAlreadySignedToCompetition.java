package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class UserAlreadySignedToCompetition extends BusinessException {
    public UserAlreadySignedToCompetition() {
        super();
    }

    public UserAlreadySignedToCompetition(String message) {
        super(message);
    }

    public UserAlreadySignedToCompetition(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadySignedToCompetition(Throwable cause) {
        super(cause);
    }

    protected UserAlreadySignedToCompetition(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
