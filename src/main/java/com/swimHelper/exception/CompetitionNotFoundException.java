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
}
