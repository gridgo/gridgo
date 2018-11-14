package io.gridgo.example.mongodb_vertx;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import org.bson.Document;
import org.joo.promise4j.Deferred;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
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

	private static final String GATEWAY_MONGO = "mongo";

	private static final String GATEWAY_VERTX = "vertx";

	private static final String MONGO_BEAN_NAME = GATEWAY_MONGO;

	private static final String APPLICATION_NAME = "gridgo-example-mongodb-vertx";

	private static final String DATABASE_NAME = "gridgo-example-mongodb-vertx";

	private static final String COLLECTION = "test";

	private static final int NUM_RECORDS = 10;

	private static final String[] NAMES = new String[] { "Peter", "Mary", "Sophie", "Bob", "Mark" };

	private int mongodbPort;

	private int httpPort;

	private GridgoContext context;

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
		 * Prepare the database, it will drop and create the collection
		 */
		try {
			prepareDatabase(mongo);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		log.info("Database prepared");

		/**
		 * Create a new GridgoContext. Each context will have its own configurations,
		 * gateways, etc. and will handle start/stop independently. A registry is
		 * required because we want to lookup the MongoClient instance later.
		 */
		var registry = new SimpleRegistry().register(MONGO_BEAN_NAME, mongo);
		context = new DefaultGridgoContextBuilder().setName(APPLICATION_NAME).setRegistry(registry).build();

		log.info("Context created");

		/**
		 * Create a gateway and attach a MongoDB connector. Because MongoDB is
		 * producer-only thus we don't need to subscribe anything
		 */
		context.openGateway(GATEWAY_MONGO) //
				.attachConnector("mongodb:" + MONGO_BEAN_NAME + "/" + DATABASE_NAME + "/" + COLLECTION);

		log.info("Mongo gateway opened");

		/**
		 * Create another gateway and attach a VertxHttp connector. We will subscribe
		 * for incoming requests and handle it
		 */
		context.openGateway(GATEWAY_VERTX) //
				.attachConnector("vertx:http://127.0.0.1:" + httpPort + "/api?method=GET") //
				.subscribe(this::handleHttpRequest);

		log.info("Vertx gateway opened");

		/**
		 * Finally start the connector, this will starts all the opened gateways and
		 * attached connectors
		 */
		context.start();

		log.info("Context started");

		/**
		 * Send some requests to populate dummy data
		 */
		try {
			populateDummyData();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		log.info("Data populated");

		/**
		 * Make sure the context will stop when application is about to shutdown
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			context.stop();
			log.info("Context stopped");
		}));
	}

	private void populateDummyData() throws InterruptedException {
		var latch = new CountDownLatch(1);
		context.findGateway(GATEWAY_MONGO) //
				.ifPresent(gateway -> {
					/**
					 * Create a message containing dummy data
					 */
					Message msg = createInsertMessage();
					/**
					 * Send it to the gateway and wait for the response
					 */
					gateway.call(msg).always((s, r, e) -> {
						latch.countDown();
					});
				});
		latch.await();
	}

	private Message createInsertMessage() {
		BReference[] list = IntStream.range(1, NUM_RECORDS) //
				.mapToObj(this::createDummyDocument) //
				.map(BReference::newDefault) //
				.toArray(size -> new BReference[size]);
		var headers = BObject.newDefault().setAny(MongoDBConstants.OPERATION, MongoDBConstants.OPERATION_INSERT);
		return Message.newDefault(Payload.newDefault(headers, BArray.newDefault(list)));
	}

	private Document createDummyDocument(int i) {
		return new Document("key", i).append("name", createRandomName());
	}

	private String createRandomName() {
		double ran = Math.random();
		int random = (int) ((NAMES.length - 1) * ran);
		return NAMES[random];
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

		// access the mongo gateway
		gc.findGateway(GATEWAY_MONGO) //
				.ifPresentOrElse(gateway -> {
					/**
					 * if we find the gateway, make a request and return response to client
					 */
					callAndReturn(deferred, gateway);
				}, () -> {
					/**
					 * if no gateway available, which it shouldn't, we should return error to client
					 */
					deferred.reject(new RuntimeException("No mongodb gateway available"));
				});
	}

	private void callAndReturn(Deferred<Message, Exception> deferred, Gateway gateway) {
		var mongoRequest = createMongoRequest();
		gateway.call(mongoRequest) // call the gateway
				.done(response -> {
					// return response if success
					deferred.resolve(response);
				}).fail(ex -> {
					// just to make sure we won't miss anything
					log.error("Error while handling request", ex);
					// return exception if failed
					deferred.reject(ex);
				});
	}

	private Message createMongoRequest() {
		var headers = BObject.newDefault() // create new headers
				.setAny(MongoDBConstants.OPERATION, MongoDBConstants.OPERATION_FIND_ALL); // set operation to find all
		return Message.newDefault(Payload.newDefault(headers, null));
	}

	private void prepareDatabase(MongoClient mongo) throws InterruptedException {
		var latch = new CountDownLatch(1);
		var db = mongo.getDatabase(DATABASE_NAME);
		db.getCollection(COLLECTION).drop((a, b) -> {
			log.info("Drop collection completed");
			db.createCollection("testCols", (result, throwable) -> {
				log.info("Create collection completed");
				latch.countDown();
			});
		});
		latch.await();
	}

	private MongoClient createMongoClient() {
		return MongoClients.create("mongodb://127.0.0.1:" + mongodbPort + "/?waitQueueMultiple=10");
	}
}
