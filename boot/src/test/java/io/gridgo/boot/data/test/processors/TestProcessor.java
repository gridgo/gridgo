package io.gridgo.boot.data.test.processors;

import java.util.List;

import io.gridgo.boot.data.support.annotations.DataAccessInject;
import io.gridgo.boot.data.test.data.User;
import io.gridgo.boot.data.test.data.UserDAO;
import io.gridgo.boot.support.annotations.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.AbstractProcessor;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import lombok.Setter;

@Setter
@Gateway("test")
public class TestProcessor extends AbstractProcessor {

    @DataAccessInject
    private UserDAO userDAO;

    @Override
    public void process(RoutingContext rc, GridgoContext gc) {
        userDAO.dropTable() //
               .pipeDone(r -> userDAO.createTable()) //
               .pipeDone(r -> userDAO.add(1, "hello")) //
               .pipeDone(r -> userDAO.find(1)) //
               .<Message, Exception>filterDone(this::transform) //
               .forward(rc.getDeferred());
    }

    private Message transform(List<User> r) {
        return Message.ofAny((r.isEmpty()) ? null : r.get(0));
    }
}
