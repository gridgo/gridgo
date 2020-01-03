package io.gridgo.core.support.transformers.impl;

import io.gridgo.bean.BElement;
import io.gridgo.framework.support.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FormattedDeserializeMessageTransformer extends AbstractBytesMessageTransformer {

    private final String format;

    @Override
    protected Message doTransform(Message msg, byte[] body) {
        return transformBody(msg, BElement.ofBytes(body, format));
    }
}
