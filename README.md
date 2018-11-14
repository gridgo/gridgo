# gridgo

Website: [https://gridgo.io](https://gridgo.io)

A platform to create distributed systems more easier

### install

```xml
<dependency>
    <groupId>io.gridgo</groupId>
    <artifactId>gridgo-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

### getting started

The entry-point of a Gridgo application is the `GridgoContext`. A `GridgoContext` will act as a standalone component which will have its own configuration and be started/stopped independently regardless of where it's running. While a JVM process is a physical entity, a `GridgoContext` is a logical one, and in fact, you can have multiple instances of `GridgoContext` inside a single JVM process.

`GridgoContext` can be created using `GridgoContextBuilder`, which currently support `DefaultGridgoContextBuilder`

```java
// create the context using default configuration
var context1 = new DefaultGridgoContextBuilder().setName("application1").build();

// you can also override the default configuration, e.g with a custom registry
// more on Registry will be discussed later
var myRegistry = new SimpleRegistry().register("someName", someEntity);
var context2 = new DefaultGridgoContextBuilder().setName("application2").setRegistry(myRegistry).build();
```

`GridgoContext` allows you to open `Gateway`. Gateway is the abstraction level between the I/O layer (Connector) and business logic code. It allows you to write code in event-driven paradigm. You can also think of Gateway as the bridge between your application business logic and the external (remote or local) endpoints.

Gateways are asynchronous in nature, which all interactions are handled using Promise.

The following code will open a new gateway, attach an I/O connector to it and subscribe for incoming messages.

```java
var gateway = context.openGateway("myGateway")
                     .attachConnector("kafka:mytopic") // you will likely to attach a connector to do something useful with gateway
                     .subscribe(this::handleMessages) // subscribe for incoming messages
                     .finishSubscribing().get();
```

More about Connector and available endpoints can be found [here](https://github.com/gridgo/gridgo-connector)

`handleMessages` is actually a implementation of `Processor`, which will take 2 arguments: a `RoutingContext` containing information about the current request and the `GridgoContext` the request is associated with.

You can also specify a condition and `ExecutionStrategy` for the subscription. If you specify a condition, the `Processor` will only be executed if the condition is satisfied.

```java
var gateway = context.openGateway("myGateway")
                     .attachConnector("kafka:mytopic") // you will likely to attach a connector to do something useful with gateway
                     .subscribe(this::handleMessages) // subscribe for incoming messages
                     .when("payload.body.data > 1") // only execute the Processor if payload body is numeric and greater than 1
                     .finishSubscribing().get();
```

After you have configured the context, you need to call its `start()` method, which will in turn starting gateways. If the attached connector supports `Consumer`, it will listen for incoming messages and route them to the matching Processors.

```java
context.start();

// Also when you are done with the context, stop it
context.stop();
// Or register a shutdown hook
Runtime.getRuntime().addShutdownHook(new Thread(context::stop);
```

### returning responses to connectors

Many Connectors require responses or acknowledgements from Processors, e.g in a HTTP server, you need to send the response back to client, or in Kafka you need to send acknowledgement back to KafkaConnector, so it will commit the message. This is done using the `Deferred` object in `RoutingContext`

```java
public void handleMessages(RoutingContext rc, GridgoContext gc) {
    // do some work to get the response
    // Gridgo favors asynchronous over synchronous, so your method shouldn't block
    rc.getDeferred().resolve(response);
    
    // or reject the request with some exception
    rc.getDeferred().reject(exception);
}
```

Only the first call to either `resolve()` or `reject()` will work. Subsequent calls will be ignored.

### sending messages to gateways

Flows between Processors and Gateways are not one-way, most of the time. Often you will need to send messages to a remote endpoint via Gateway, e.g querying a database, or producing messages to Kafka brokers. To do so you must first obtain the Gateway instance, e.g using `GridgoContext`

```java
var gateway = context.findGateway("myGateway") // will return an Optional<Gateway>
                     .ifPresent(gateway -> {
                         // send message here
                     });
```

There are 5 different types of sending:

- `void send(Message)`: Send a message to the attached conectors and forget about it. You won't know if the transportation has been successful or not
- `Promise sendWithAck(Message)`: Send a message to the attached conectors with acknowledgement. You will know the status of the transportation, but don't know about the response.
- `Promise call(Message)`: Send a message to the attached conectors and get the response. This is called RPC mode. Some connectors might not support it.
- `void push(Message)`: Simply put the message into the incoming sink of the Gateway and make it available for Processors. This operation won't involve any I/O.
- `void callAndPush(Message)`: This is similar to `Promise call(Message)`, but the response is put into the incoming sink of the Gateway instead of returning to Processor. This will make your application logic cleaner and independent of I/O, at the cost of logic fragmentation. This is inspired by the LMAX architecture.

### multiple connectors per gateway

It is possible to attach multiple connectors to a single Gateway. Doing so will make incoming messages from all attached Connectors to be routed to the Processors. One more interesting thing is that when you send messages to Gateway (using either `send()`, `sendWithAck()`, `call()` or `callAndPush()`), the messages will also be multiplexed to all Connectors.

So what is the response if you make RPC calls to a Gateway having multiple Connectors? Well, Gridgo allows you choose the strategy to compose the response, using `ProducerTemplate`. There are 3 built-in types of `ProducerTemplate`:

- `SingleProducerTemplate`: which will keep the first Connector response and discard all others, this is the default template
- `JoinProducerTemplate`: which will merge all responses into a single `MultipartMessage`
- `MatchingProducerTemplate`: similar to `JoinProducerTemplate`, but allows you to use a `Predicate` to filter what Connector to be called. Responses are also merged into a single `MultipartMessage`

### continuous integration

**master**

[![Build Status](https://travis-ci.org/gridgo/gridgo.svg?branch=master)](https://travis-ci.org/gridgo/gridgo)
[![Coverage Status](https://coveralls.io/repos/github/gridgo/gridgo/badge.svg?branch=master&maxAge=86400)](https://coveralls.io/github/gridgo/gridgo?branch=master)

**develop**

[![Build Status](https://travis-ci.com/gridgo/gridgo.svg?branch=develop)](https://travis-ci.com/gridgo/gridgo)
[![Coverage Status](https://coveralls.io/repos/github/gridgo/gridgo/badge.svg?branch=develop&maxAge=86400)](https://coveralls.io/github/gridgo/gridgo?branch=develop)

### license

This library is distributed under MIT license, see [LICENSE](LICENSE)
