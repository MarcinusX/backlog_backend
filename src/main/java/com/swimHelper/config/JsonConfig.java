//package com.swimHelper.config;
//
//import com.google.gson.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.lang.reflect.Type;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//
///**
// * Created by Marcin Szalek on 25.07.17.
// */
//@Configuration
//public class JsonConfig {
//    @Bean
//    public Gson gson() {
//        return new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
//                .create();
//    }
//
//    class LocalDateAdapter implements JsonSerializer<LocalDateTime> {
//        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
//            return new JsonPrimitive(date.format(DateTimeFormatter.ofP)); // "yyyy-mm-dd"
//        }
//    }
//}
