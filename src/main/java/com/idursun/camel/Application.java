package com.idursun.camel;

import com.idursun.camel.config.HawtioConfiguration;
import io.hawt.config.ConfigFacade;
import io.hawt.system.ConfigManager;
import io.hawt.web.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@Import(HawtioConfiguration.class)
public class Application {

    @Autowired
    ServletContext servletContext;

    public static void main(String[] args) {
       System.setProperty(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED, "false");
       SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        final ConfigManager configManager = new ConfigManager();
        configManager.init();
        servletContext.setAttribute("ConfigManager", configManager);
    }

    @Bean
    public ConfigFacade configFacade() throws Exception {
        ConfigFacade config = new ConfigFacade() {
            public boolean isOffline() {
                return true;
            }
        };
        config.init();
        return config;
    }

}
