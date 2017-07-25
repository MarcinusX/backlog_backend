package com.swimHelper.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Marcin Szalek on 25.07.17.
 */
@Configuration
public class JsonConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
