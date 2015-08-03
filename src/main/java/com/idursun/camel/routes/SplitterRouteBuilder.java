package com.idursun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SplitterRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://timer1?period=200&daemon=false").routeId("splitter")
                .split(body()).streaming()
                .setHeader("sku", body())
                .log("received message with ${body}").to("mock:splitter");

    }
}
