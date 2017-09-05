package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class CompetitionNotFoundException extends BusinessException {
    public CompetitionNotFoundException() {
        super();
    }

    public CompetitionNotFoundException(String message) {
        super(message);
    }

    public CompetitionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompetitionNotFoundException(Throwable cause) {
        super(cause);
    }

    protected CompetitionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
