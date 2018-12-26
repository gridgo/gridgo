package io.gridgo.example.mongodb_vertx;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

import io.gridgo.bean.BObject;
import io.gridgo.connector.mongodb.MongoDBConstants;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoVertxApplication implements Runnable {

    /**
     * The name of the Mongo Gateway. We can use the name to open the gateway and
     * look it up later
     */
    private static final String GATEWAY_MONGO = "mongo";

    /**
     * The name of the Vert.x HTTP Gateway
     */
    private static final String GATEWAY_VERTX = "vertx";

    /**
     * The name of the Mongo bean. It will be used to register the MongoClient bean
     */
    private static final String MONGO_BEAN_NAME = GATEWAY_MONGO;

    /**
     * Our application name
     */
    private static final String APPLICATION_NAME = "gridgo-example-mongodb-vertx";

    /**
     * The name of the database
     */
    private static final String DATABASE_NAME = "gridgo-example-mongodb-vertx";

    /**
     * The name of the Mongo collection
     */
    private static final String COLLECTION = "test";

    private int mongodbPort;

    private int httpPort;

    private GridgoContext context;

    private Gateway mongoGateway;

    public MongoVertxApplication(int mongodbPort, int httpPort) {
        this.mongodbPort = mongodbPort;
        this.httpPort = httpPort;
    }

    @Override
    public void run() {
        /**
         * Creating a new MongoClient, this is required by gridgo-mongodb connector
         */
        var mongo = createMongoClient();

        log.info("MongoClient created");

        /**
         * Prepare the database, it will drop and create the collection and also
         * populate some dummy data
         */
        try {
            new MongoHelper().prepareDatabase(mongo, DATABASE_NAME, COLLECTION);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        log.info("Database prepared");

        /**
         * Create a new GridgoContext. Each context will have its own configurations,
         * gateways, etc. and will handle start/stop independently.
         * 
         * A registry is required here because we want to lookup the MongoClient
         * instance later. We will create a simple registry and register the MongoClient
         * instance.
         */
        var registry = new SimpleRegistry().register(MONGO_BEAN_NAME, mongo);
        context = new DefaultGridgoContextBuilder().setName(APPLICATION_NAME).setRegistry(registry).build();

        log.info("Context created");

        /**
         * Create another gateway and attach a VertxHttp connector. We will subscribe
         * for incoming requests and handle it.
         * 
         * The Vert.x HTTP endpoint syntax is
         * <code>vertx:http://{adddress}:{port}/[{path}][?options]</code>
         */
        context.openGateway(GATEWAY_VERTX) //
               .attachConnector("vertx:http://127.0.0.1:" + httpPort + "/api?method=GET") //
               .subscribe(this::handleHttpRequest);

        log.info("Vertx gateway opened");

        /**
         * Create a gateway and attach a MongoDB connector. Because MongoDB is
         * producer-only thus we don't need to subscribe anything.
         * 
         * The MongoDB endpoint syntax is
         * <code>mongodb:{mongoBeanName}/{database}/{collection}[?options]</code>
         * 
         * Also we will store the gateway instance to be used later
         */
        var mongoEndpoint = "mongodb:" + MONGO_BEAN_NAME + "/" + DATABASE_NAME + "/" + COLLECTION;
        mongoGateway = context.openGateway(GATEWAY_MONGO) // open the gateway
                              .attachConnector(mongoEndpoint) // attach the MongoDB connector
                              .get(); // get the gateway instance

        log.info("Mongo gateway opened");

        /**
         * Finally start the connector, this will starts all the opened gateways and
         * attached connectors
         */
        context.start();

        log.info("Context started");

        /**
         * Make sure the context will stop when application is about to shutdown
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            context.stop();
            log.info("Context stopped");
        }));
    }

    /**
     * Handle incoming HTTP requests.
     * 
     * In this example we will fetch all records from mongo and return it to client
     * as JSON
     */
    private void handleHttpRequest(RoutingContext rc, GridgoContext gc) {
        // get the deferred object. this is used by VertxHttp connector to return
        // response to client
        var deferred = rc.getDeferred();

        // call the gateway with a default request and return response to client
        var mongoRequest = createMongoRequest();
        mongoGateway.call(mongoRequest) // call the gateway
                    .done(deferred::resolve) //
                    .fail(ex -> {
                        // just to make sure we won't miss anything
                        log.error("Error while handling request", ex);
                        // return exception if failed
                        deferred.reject(ex);
                    });
    }

    private Message createMongoRequest() {
        var headers = BObject.ofEmpty() // create new headers
                             .setAny(MongoDBConstants.OPERATION, MongoDBConstants.OPERATION_FIND_ALL); // set operation
                                                                                                       // to find all
        return Message.of(Payload.of(headers, null));
    }

    private MongoClient createMongoClient() {
        return MongoClients.create("mongodb://127.0.0.1:" + mongodbPort + "/?waitQueueMultiple=10");
    }
}
