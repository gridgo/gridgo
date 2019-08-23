package io.gridgo.connector.support;

import org.joo.promise4j.Deferred;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.Message;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeferredAndRoutingId {

    private Deferred<Message, Exception> deferred;

    private BValue routingId;
}
