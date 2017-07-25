package com.swimHelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

/**
 * Created by mstobieniecka on 2017-07-20.
 */
@Configuration
public class TrainingGeneratorConfig {
    @Bean
    public Random getRandom() {
        return new Random();
    }
}
