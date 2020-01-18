package io.gridgo.core.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static io.gridgo.core.support.LifecycleType.COMPONENT_ATTACHED;
import static io.gridgo.core.support.LifecycleType.COMPONENT_STARTED;
import static io.gridgo.core.support.LifecycleType.COMPONENT_STOPPED;
import static io.gridgo.core.support.LifecycleType.CONNECTOR_ATTACHED;
import static io.gridgo.core.support.LifecycleType.CONNECTOR_STARTED;
import static io.gridgo.core.support.LifecycleType.CONNECTOR_STOPPED;
import static io.gridgo.core.support.LifecycleType.CONTEXT_STARTED;
import static io.gridgo.core.support.LifecycleType.CONTEXT_STOPPED;
import static io.gridgo.core.support.LifecycleType.GATEWAY_CLOSED;
import static io.gridgo.core.support.LifecycleType.GATEWAY_OPENED;
import static io.gridgo.core.support.LifecycleType.GATEWAY_STARTED;
import static io.gridgo.core.support.LifecycleType.GATEWAY_STOPPED;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.ContextAwareComponent;

public class LifecycleUnitTest {

    public static class CustomComponent implements ContextAwareComponent {

        @Override
        public String getName() {
            return "test_component";
        }

        @Override
        public void setContext(GridgoContext context) {

        }
    }

    public static class CustomConnector extends AbstractConnector {

        @Override
        public String getName() {
            return "test_connector";
        }
    }

    @Test
    public void testComplete() {
        var context = new DefaultGridgoContextBuilder().setName("test_app").build();
        context.attachComponent(new CustomComponent());
        context.openGateway("test_gateway")
               .attachConnector(new CustomConnector());
        context.start();
        context.stop();
        context.closeGateway("test_gateway");

        var eventSet = new HashSet<>(List.of(CONTEXT_STARTED, COMPONENT_ATTACHED, GATEWAY_OPENED, CONNECTOR_ATTACHED,
                COMPONENT_STARTED, CONNECTOR_STARTED, GATEWAY_STARTED, CONNECTOR_STOPPED, GATEWAY_STOPPED,
                COMPONENT_STOPPED, CONTEXT_STOPPED, GATEWAY_CLOSED));
        context.subscribe(event -> {
            if (!eventSet.remove(event.getType())) {
                throw new RuntimeException("Unexpected event: " + event.getType());
            }
        });
        Assert.assertTrue(eventSet.isEmpty());
    }
}
