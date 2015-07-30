package com.idursun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://foo?period=5000&daemon=false").
                setBody().constant("<hello>world!</hello>").
                log(">>> ${body}").to("mock:result");
    }
}
