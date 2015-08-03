package com.idursun.camel.test;

import com.idursun.camel.config.RoutesConfig;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.HashMap;

public abstract class RouteTestBase extends CamelSpringTestSupport {

    private HashMap<String,AdviceWithRouteBuilder> routeBuilderAdviceList = new HashMap<>();

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

    public abstract void createAdviseWithRouteBuilders();

    protected void customizeRoute(String routeId, AdviceWithRouteBuilder adviceWithRouteBuilder) {
        assert adviceWithRouteBuilder != null;
        routeBuilderAdviceList.put(routeId, adviceWithRouteBuilder);
    }

    @Before
    public void applyAdviseWithRouteBuilder() throws Exception {
        createAdviseWithRouteBuilders();

        for (String routeId : routeBuilderAdviceList.keySet()) {
            RouteDefinition routeDefinition = context.getRouteDefinition(routeId);
            if (routeDefinition == null)
                continue;

            routeDefinition.adviceWith(context, routeBuilderAdviceList.get(routeDefinition.getId()));
        }
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

}
