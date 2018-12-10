package io.gridgo.example.tiktactoe;

import static io.gridgo.example.tiktactoe.TikTacToeConstants.ROUTING_ID;
import static io.gridgo.socket.SocketConstants.SOCKET_MESSAGE_TYPE;

import io.gridgo.bean.BValue;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.impl.BridgeComponent;
import io.gridgo.example.tiktactoe.comp.TikTacToeGameServer;
import io.gridgo.example.tiktactoe.comp.TikTacToeHttp;
import io.gridgo.framework.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;

public class TikTacToe extends NonameComponentLifecycle {

    private static final String GATEWAY_HTTP = "tikTacToeHttp";

    private static final String GATEWAY_WEBSOCKET = "tikTacToeWebsocket";

    private static final String GATEWAY_GAME = "tikTacToeGame";

    private static final String GATEWAY_FORWARDER = "tikTacToeForwarder";

    private final GridgoContext appContext;

    public TikTacToe() {
        this.appContext = new DefaultGridgoContextBuilder().setName("tiktactoe").setExceptionHandler(this::onException)
                                                           .build();

        appContext.openGateway(GATEWAY_HTTP) //
                  .attachConnector("jetty:http://0.0.0.0:8888/[tiktactoe/*]");

        appContext.openGateway(GATEWAY_WEBSOCKET) //
                  .attachConnector("netty4:server:ws://0.0.0.0:8889/tiktactoe");

        appContext.openGateway(GATEWAY_FORWARDER) //
                  .attachConnector("zmq:push:ipc://clientToGame") //
                  .attachConnector("zmq:pull:ipc://gameToClient");

        appContext.openGateway(GATEWAY_GAME) //
                  .attachConnector("zmq:pull:ipc://clientToGame") //
                  .attachConnector("zmq:push:ipc://gameToClient");

        this.appContext //
                       // handle http request
                       .attachComponent(new TikTacToeHttp(GATEWAY_HTTP)) //
                       // handle game logic request
                       .attachComponent(new TikTacToeGameServer(GATEWAY_GAME)) //
                       // attach 2 bridge components to fwd msg from/to websocket to/from game via zmq
                       .attachComponent(
                               new BridgeComponent(GATEWAY_WEBSOCKET, GATEWAY_FORWARDER, this::forwardMsgWsToGame)) //
                       .attachComponent(
                               new BridgeComponent(GATEWAY_FORWARDER, GATEWAY_WEBSOCKET, this::forwardMsgGameToWs)) //
        ;
    }

    private void onException(Throwable ex) {
        getLogger().error("Internal error", ex);
    }

    private Message forwardMsgWsToGame(Message input) {
        Payload payload = input.getPayload();
        if (payload == null) {
            payload = Payload.of(null);
            input.setPayload(payload);
        }
        payload.addHeader(SOCKET_MESSAGE_TYPE, input.getMisc().get(SOCKET_MESSAGE_TYPE));
        payload.addHeader(ROUTING_ID, input.getRoutingId().orElse(BValue.ofEmpty()).getData());
        return input;
    }

    private Message forwardMsgGameToWs(Message input) {
        input.setRoutingId(input.getPayload().getHeaders().getValue(ROUTING_ID));
        input.getPayload().getHeaders().remove(ROUTING_ID);
        return input;
    }

    @Override
    protected void onStart() {
        this.appContext.start();
    }

    @Override
    protected void onStop() {
        this.appContext.stop();
    }
}
