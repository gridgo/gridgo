package io.gridgo.example.tiktactoe.comp;

import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.connector.httpcommon.HttpCommonConstants;
import io.gridgo.connector.httpcommon.HttpContentType;
import io.gridgo.connector.httpcommon.HttpStatus;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;

public class TikTacToeHttp extends TikTacToeBaseComponent {

    public TikTacToeHttp(String gatewayName) {
        super(gatewayName);
    }

    protected void processRequest(RoutingContext rc, GridgoContext gc) {
        Message msg = rc.getMessage();

        var payload = msg.getPayload();
        var path = payload.getHeaders().getString(HttpCommonConstants.PATH_INFO);
        if (path != null) {
            path = path.trim();
            if (path.startsWith("/")) {
                path = path.substring(1, path.length());
            }
        }

        if (path == null || path.isBlank()) {
            path = "index.html";
        }
        path = "webcontent/" + path;
        Payload response;
        var input = getClass().getClassLoader().getResourceAsStream(path);
        if (input == null) {
            response = Payload.of(BValue.of("Not found")).addHeader(HttpCommonConstants.HEADER_STATUS,
                    HttpStatus.NOT_FOUND_404.getCode());
        } else {
            response = Payload.of(BReference.of(input)).addHeader(HttpCommonConstants.CONTENT_TYPE,
                    HttpContentType.forFileName(path).getMime());
        }
        rc.getDeferred().resolve(Message.of(response).setRoutingId(msg.getRoutingId().orElseThrow()));
    }

}
