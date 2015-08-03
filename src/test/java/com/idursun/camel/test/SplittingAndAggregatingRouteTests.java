package com.idursun.camel.test;

import com.idursun.camel.routes.SplittingAndAggregatingRouteBuilder;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import java.util.Arrays;

public class SplittingAndAggregatingRouteTests extends RouteTestBase {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SplittingAndAggregatingRouteBuilder();
    }

    @Override
    public void createAdviseWithRouteBuilders() {
        customizeRoute("splitting-products", new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:products");

                interceptSendToEndpoint("http://product/P1")
                        .skipSendToOriginalEndpoint()
                        .setBody(constant("{ \"name\" : \"Product 1\", \"sku\": \"P1\" }"));
            }
        });

        customizeRoute("splitting-listings", new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:listings");

                interceptSendToEndpoint("http://listing/LIST1")
                        .skipSendToOriginalEndpoint()
                        .setBody(constant("{ \"name\" : \"Listing 1\", \"sku\": \"P1\", \"code\": \"LIST1\" }"));

                interceptSendToEndpoint("http://listing/LIST2")
                        .skipSendToOriginalEndpoint()
                        .setBody(constant("{ \"name\" : \"Product 1\", \"sku\": \"P1\", \"code\": \"LIST2\" }}"));
            }
        });

        customizeRoute("splitting-and-aggregating", new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("couchdb:{{couchdb.url}}").skipSendToOriginalEndpoint().to("mock:couchdb");
            }
        });

    }

    @EndpointInject(uri = "mock:couchdb")
    MockEndpoint aggregatedMockEndPoint;

    @Test
    public void test_aggregates() throws Exception {
        aggregatedMockEndPoint.expectedMessageCount(1);

        context.start();

        template.sendBody("direct:products", Arrays.asList("P1"));
        template.sendBody("direct:listings", Arrays.asList("LIST1", "LIST2"));

        aggregatedMockEndPoint.assertIsSatisfied();

        context.stop();

    }

}
