package com.idursun.camel.test;

import com.idursun.camel.config.RoutesConfig;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public abstract class RouteTestBase extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CamelAutoConfiguration.class, RoutesConfig.class);
        try {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addLast(new ResourcePropertySource("classpath:application.properties"));
        } catch (IOException e) {
        }

        return applicationContext;
    }

    public abstract AdviceWithRouteBuilder createAdviseWithRouteBuilder();

    @Before
    public void mockEndpoints() throws Exception{
        AdviceWithRouteBuilder adviseWithRouteBuilder = createAdviseWithRouteBuilder();
        if (adviseWithRouteBuilder != null) {
            context.getRouteDefinitions().get(0).adviceWith(context, adviseWithRouteBuilder);
        }
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

}
