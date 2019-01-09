package io.gridgo.extras.flink;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.framework.support.Message;
import io.reactivex.disposables.Disposable;
import lombok.Setter;

public class GatewaySource implements ContextAwareComponent, SourceFunction<Message> {

    private static final long serialVersionUID = -7568626695827640831L;

    private String gatewayName;

    @Setter
    private GridgoContext context;

    private Disposable disposable;

    public GatewaySource(String name) {
        this.gatewayName = name;
    }

    @Override
    public String getName() {
        return "flink.source.gateway";
    }

    @Override
    public void run(SourceContext<Message> ctx) throws Exception {
        var gateway = this.context.findGatewayMandatory(gatewayName);
        this.disposable = gateway.asObservable().forEach(rc -> {
            ctx.collect(rc.getMessage());
        });
    }

    @Override
    public void cancel() {
        if (disposable != null)
            disposable.dispose();
    }
}
