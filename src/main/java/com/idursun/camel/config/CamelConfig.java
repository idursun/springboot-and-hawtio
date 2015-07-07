package com.idursun.camel.config;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.properties.PropertiesComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

    @Bean
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer://foo?period=5000&daemon=false").
                setBody().constant("<hello>world!</hello>").
                log(">>> ${body}");
            }
        };
    }

//    @Override
//    protected void setupCamelContext(CamelContext camelContext) throws Exception {
//        // make Camel aware of Spring Boot’s application.properties
//        PropertiesComponent pc = new PropertiesComponent();
//        pc.setLocation("classpath:application.properties");
//        camelContext.addComponent("properties", pc);
//
//        // enable performance metrics
//        camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
//
//        super.setupCamelContext(camelContext);
//    }
}
