package io.gridgo.boot.data.support.impl;

import io.gridgo.bean.BObject;
import io.gridgo.boot.data.support.annotations.JdbcProduce;
import io.gridgo.framework.support.Message;
import lombok.Getter;

@Getter
public class JdbcDataAccessHandler extends AbstractDataAccessHandler<JdbcProduce> {

    public JdbcDataAccessHandler() {
        super(JdbcProduce.class);
    }

    protected Message buildMessage(JdbcProduce annotation, Object[] args) {
        var headers = BObject.ofEmpty();
        for (int i = 0; i < args.length; i++) {
            headers.setAny((i + 1) + "", args[i]);
        }
        return Message.ofAny(headers, annotation.value());
    }
}
