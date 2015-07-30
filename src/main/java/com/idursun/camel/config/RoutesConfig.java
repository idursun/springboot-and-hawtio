package com.idursun.camel.config;

import com.idursun.camel.routes.utils.RandomGroupGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

   @Bean
    public RandomGroupGenerator randomGroupGenerator() {
       return new RandomGroupGenerator();
   }
}
