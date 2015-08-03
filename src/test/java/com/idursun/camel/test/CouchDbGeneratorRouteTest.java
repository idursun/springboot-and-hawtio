package com.idursun.camel.test;

import com.idursun.camel.routes.CouchDbGeneratorRouteBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;

public class CouchDbGeneratorRouteTest extends RouteTestBase {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CouchDbGeneratorRouteBuilder();
    }

    @Before
    public void createAdviseWithRouteBuilder() throws Exception {
    }

    @Override
    public void createAdviseWithRouteBuilders() {

        customizeRoute("couchdb-producer", new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:input");
                interceptSendToEndpoint("couchdb:{{couchdb.url}}")
                        .skipSendToOriginalEndpoint()
                        .to("mock:couchdb");
            }
        });

    }

    @Test
    public void testMessageIsProduced() throws Exception {
        getMockEndpoint("mock:couchdb").expectedMessageCount(3);

        context.start();

        template.asyncSendBody("direct:input", null);
        template.asyncSendBody("direct:input", null);
        template.asyncSendBody("direct:input", null);

        assertMockEndpointsSatisfied();

        context.stop();
    }

}
