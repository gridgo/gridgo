package io.gridgo.example.mongodb_vertx;

public class Main {

    private static final String PROPERTY_MONGODB_PORT = "mongodb.port";
    private static final String DEFAULT_MONGODB_PORT = "27017";
    private static final String PROPERTY_HTTP_PORT = "http.port";
    private static final String DEFAULT_HTTP_PORT = "8088";

    /**
     * Run the application. This example requires MongoDB service on port 27017. You
     * can also change the port using -Dmongodb.port=YOUR_PORT_HERE
     * 
     * It will start a HTTP server on port 8088. You can also change it using
     * -Dhttp.port=YOUR_PORT_HERE
     * 
     * After it is running you can access http://localhost:8088/api in your browser
     * Beware that it will drop and create a database named
     * "gridgo-example-mongodb-vertx" every time you run it
     * 
     * @param args
     */
    public static void main(String[] args) {
        int mongodbPort = Integer.parseInt(System.getProperty(PROPERTY_MONGODB_PORT, DEFAULT_MONGODB_PORT));
        int httpPort = Integer.parseInt(System.getProperty(PROPERTY_HTTP_PORT, DEFAULT_HTTP_PORT));
        var app = new MongoVertxApplication(mongodbPort, httpPort);
        app.run();
    }
}
