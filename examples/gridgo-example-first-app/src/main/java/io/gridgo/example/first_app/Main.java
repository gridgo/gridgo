package io.gridgo.example.first_app;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;

public class Main {

    private static final String VERTX_URL = "vertx:http://127.0.0.1:8080/";

    private static final String APPLICATION_NAME = "application";

    private static final String GATEWAY_NAME = "myGateway";

    /**
     * Run the application.
     * 
     * It will start a HTTP server on port 8080.
     * 
     * After it is running you can access http://localhost:8080 in your browser
     * 
     * @param args
     */
    public static void main(String[] args) {
        var context = new DefaultGridgoContextBuilder().setName(APPLICATION_NAME).build();

        context.openGateway(GATEWAY_NAME) //
               .attachConnector(VERTX_URL) // attach a web server connector
               .subscribe(Main::handleMessages); // subscribe for incoming messages

        context.start();

        Runtime.getRuntime().addShutdownHook(new Thread(context::stop));
    }

    private static void handleMessages(RoutingContext rc, GridgoContext gc) {
        var msg = rc.getMessage();
        var deferred = rc.getDeferred();

        // using the same request as response
        deferred.resolve(Message.of(Payload.of(msg.getPayload().getBody())));
    }
}
