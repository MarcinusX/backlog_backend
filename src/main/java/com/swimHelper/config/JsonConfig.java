package com.swimHelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;


/**
 * Created by Marcin Szalek on 25.07.17.
 */
@Configuration
public class JsonConfig {
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .create();
    }

//    class LocalDateAdapter implements JsonSerializer<LocalDateTime> {
//        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
//            return new JsonPrimitive(date.format(DateTimeFormatter.ofP)); // "yyyy-mm-dd"
//        }
//    }
}
