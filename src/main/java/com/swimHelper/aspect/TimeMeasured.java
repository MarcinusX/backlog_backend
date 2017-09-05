package com.swimHelper.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Marcin Szalek on 25.08.17.
 */
@Target(ElementType.METHOD)
public @interface TimeMeasured {
}
