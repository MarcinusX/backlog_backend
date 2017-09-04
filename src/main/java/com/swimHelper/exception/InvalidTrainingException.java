package com.swimHelper.exception;

/**
 * Created by mstobieniecka on 2017-09-01.
 */
public class InvalidTrainingException extends BusinessException {
    public InvalidTrainingException() {
        super();
    }

    public InvalidTrainingException(String message) {
        super(message);
    }
}
