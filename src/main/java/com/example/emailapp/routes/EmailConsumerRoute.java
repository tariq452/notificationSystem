package com.example.emailapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:email_notifications?brokers=kafka:9092&groupId=email-group")
                .routeId("kafka-to-email")
                .log("Processing notification by Pod")
                .unmarshal().json()
                .log("TEST MODE: Marking email as SENT for ${body.email}")
                .to("jdbc:dataSource");
    }
}
