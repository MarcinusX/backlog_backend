package com.swimHelper.exception;

/**
 * Created by mstobieniecka on 2017-09-01.
 */
public class TrainingNotFoundException extends BusinessException {
    public TrainingNotFoundException() {
    }

    public TrainingNotFoundException(String message) {
        super(message);
    }
}
