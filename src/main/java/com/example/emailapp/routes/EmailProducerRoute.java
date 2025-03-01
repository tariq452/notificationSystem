package com.example.emailapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmailProducerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:readDB?period=1000")
                .routeId("db-to-kafka")
                .log("Fetching new notifications")
                .to("jdbc:dataSource")
                .split(body())
                .marshal().json()
                .to("kafka:email_notifications?brokers=kafka:9092")
                .to("jdbc:dataSource");
    }
}
