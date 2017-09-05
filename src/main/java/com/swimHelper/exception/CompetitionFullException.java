package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class CompetitionFullException extends BusinessException {
    public CompetitionFullException() {
        super();
    }

    public CompetitionFullException(String message) {
        super(message);
    }

    public CompetitionFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompetitionFullException(Throwable cause) {
        super(cause);
    }

    protected CompetitionFullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
