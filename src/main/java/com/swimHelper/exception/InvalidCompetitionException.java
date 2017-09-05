package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class InvalidCompetitionException extends BusinessException {
    public InvalidCompetitionException() {
        super();
    }

    public InvalidCompetitionException(String message) {
        super(message);
    }

    public InvalidCompetitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCompetitionException(Throwable cause) {
        super(cause);
    }

    protected InvalidCompetitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
