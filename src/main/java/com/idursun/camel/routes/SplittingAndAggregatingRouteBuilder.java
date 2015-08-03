package com.idursun.camel.routes;

import com.idursun.camel.routes.utils.Listing;
import com.idursun.camel.routes.utils.Product;
import com.idursun.camel.routes.utils.ProductWithListings;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class SplittingAndAggregatingRouteBuilder extends RouteBuilder {

    public static final String TOTAL_LISTINGS = "TotalListings";

    @Override
    public void configure() throws Exception {
        from("timer://timer1?period=200&daemon=false").routeId("splitting-products")
                .split(body()).streaming()
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .recipientList(simple("http://product/${body}"))
                .setHeader("entity", constant("product"))
                .unmarshal().json(JsonLibrary.Jackson, Product.class)
                .setHeader("sku", simple("${body.getSku}"))
                .to("direct:aggregate");


        from("timer://timer1?period=200&daemon=false").routeId("splitting-listings")
                .setHeader(TOTAL_LISTINGS, simple("${body.size}"))
                .split(body()).streaming()
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .recipientList(simple("http://listing/${body}"))
                .setHeader("entity", constant("listing"))
                .unmarshal().json(JsonLibrary.Jackson, Listing.class)
                .setHeader("sku", simple("${body.getSku}"))
                .to("direct:aggregate");

        from("direct:aggregate").routeId("splitting-and-aggregating")
                .aggregate(header("sku"), new AggregationStrategy() {

                    @Override
                    public Exchange aggregate(Exchange aggregateExchange, Exchange incomingExchange) {
                        Exchange exchange = aggregateExchange;
                        if (exchange == null) {
                            exchange = incomingExchange;
                        }

                        ProductWithListings productWithListings = exchange.getIn().getBody(ProductWithListings.class);
                        if (productWithListings == null) {
                            productWithListings = new ProductWithListings();
                        }

                        if (incomingExchange.getIn().getHeader("entity").equals("product")) {
                            productWithListings.setProduct(incomingExchange.getIn().getBody(Product.class));
                        } else {
                            productWithListings.getListings().add(incomingExchange.getIn().getBody(Listing.class));
                            if (exchange.getIn().getHeader(TOTAL_LISTINGS) == null) {
                                exchange.getIn().setHeader(TOTAL_LISTINGS, incomingExchange.getIn().getHeader(TOTAL_LISTINGS));
                            }
                        }

                        Integer left = exchange.getIn().getHeader(TOTAL_LISTINGS, Integer.class);
                        if (left != null) {
                            left = left -1;
                            if (left > 0) {
                                exchange.getIn().setHeader(TOTAL_LISTINGS, left);
                            }

                            if (left <= 0 && productWithListings.getProduct() != null) {
                                exchange.getIn().setHeader(Exchange.BATCH_COMPLETE, Boolean.TRUE);
                            }
                        }

                        exchange.getIn().setBody(productWithListings);
                        return exchange;
                    }
                }).completionPredicate(header(Exchange.BATCH_COMPLETE).isEqualTo(Boolean.TRUE))
                .to("couchdb:{{couchdb.url}}");

    }
}
