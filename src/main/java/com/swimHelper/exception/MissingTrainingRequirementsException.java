package com.swimHelper.exception;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class MissingTrainingRequirementsException extends BusinessException {
    public MissingTrainingRequirementsException() {
        super();
    }

    public MissingTrainingRequirementsException(String message) {
        super(message);
    }
}
