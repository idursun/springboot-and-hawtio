package com.idursun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchDbGeneratorRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://timer1?period=200&daemon=false").routeId("couchdb-producer").
                setBody().simple("{ groupid: ${bean:randomGroupGenerator.next} }").
                to("mock:couchdb:{{couchdb.url}}");

    }
}
