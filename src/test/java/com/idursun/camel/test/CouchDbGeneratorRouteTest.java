package com.idursun.camel.test;

import com.idursun.camel.config.RoutesConfig;
import com.idursun.camel.routes.CouchDbGeneratorRouteBuilder;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class CouchDbGeneratorRouteTest extends CamelSpringTestSupport {

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

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CouchDbGeneratorRouteBuilder();
    }

    @Before
    public void mockEndPoints() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:input");
                weaveByToString(".*couchdb.*").replace().to("mock:couchdb");
            }
        });
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @EndpointInject(uri = "mock:couchdb")
    MockEndpoint mockEndPoint;

    @Test
    public void testMessageIsProduced() throws Exception {
        mockEndPoint.expectedMessageCount(3);

        context.start();

        template.asyncSendBody("direct:input", null);
        template.asyncSendBody("direct:input", null);
        template.asyncSendBody("direct:input", null);

        mockEndPoint.assertIsSatisfied();
    }
}
