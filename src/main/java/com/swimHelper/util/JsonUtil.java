package com.swimHelper.util;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * Created by Marcin Szalek on 25.07.17.
 */
@Component
public class JsonUtil {

    private final Gson gson;

    public JsonUtil(Gson gson) {
        this.gson = gson;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public String toJson(Object object, Type typeOfObj) {
        return gson.toJson(object, typeOfObj);
    }
}
