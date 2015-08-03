package com.idursun.camel.test;

import com.idursun.camel.routes.SplitterRouteBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

import java.util.HashSet;

public class SplitterRouteTest extends RouteTestBase {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SplitterRouteBuilder();
    }

    @Override
    public void createAdviseWithRouteBuilders() {
        customizeRoute("splitter", new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:input");
                mockEndpoints();
            }
        });
    }

    @Test
    public void should_split_messages() throws Exception {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:splitter");

        HashSet<String> set = new HashSet<>();
        set.add("BKD001");
        set.add("BKD002");
        set.add("BKD003");
        set.add("BKD004");

        mockEndpoint.setExpectedMessageCount(set.size());
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("sku", set.toArray());

        context.start();


        template.sendBody("direct:input", set);

        assertMockEndpointsSatisfied();
    }

}
