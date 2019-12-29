package io.gridgo.bean.serialization.json.codec;

import static com.dslplatform.json.JsonWriter.COMMA;
import static com.dslplatform.json.JsonWriter.SEMI;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.utils.pojo.getter.PojoGetter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BReferenceCompactJsonCodec extends BReferenceJsonCodec {

    @Override
    public void write(JsonWriter writer, BReference value) {
        Object reference;
        if (value == null || (reference = value.getReference()) == null) {
            writer.writeNull();
            return;
        }

        var lengthStack = new Stack<Integer>();
        var indexStack = new Stack<AtomicInteger>();
        var keyRef = new AtomicReference<String>(null);
        var waitingForComma = new AtomicBoolean(false);

        PojoGetter.of(reference, value.getterProxy()) //
                .shallowly(true) //
                .walker((indicator, val, signature, proxy) -> {
                    switch (indicator) {
                    case START_MAP:
                    case START_ARRAY:
                        tryWriteComma(writer, waitingForComma);
                        tryWriteWaitingKey(writer, keyRef);
                        writer.writeByte(indicator == START_ARRAY ? JsonWriter.ARRAY_START : JsonWriter.OBJECT_START);

                        waitingForComma.set(false);
                        lengthStack.push((int) val);
                        indexStack.push(new AtomicInteger(0));

                        if (indexStack.size() > 0)
                            indexStack.peek().incrementAndGet();
                        break;
                    case END_MAP:
                    case END_ARRAY:
                        indexStack.pop();
                        lengthStack.pop();
                        writer.writeByte(indicator == END_ARRAY ? JsonWriter.ARRAY_END : JsonWriter.OBJECT_END);
                        waitingForComma.set(true);
                        break;
                    case KEY:
                        keyRef.set((String) val);
                        break;
                    case KEY_NULL:
                        indexStack.peek().incrementAndGet();
                        break;
                    case VALUE:
                        tryWriteComma(writer, waitingForComma);
                        tryWriteWaitingKey(writer, keyRef);

                        var ele = BElement.wrapAny(val);
                        if (ele.isReference())
                            ele.asReference().getterProxy(proxy);

                        writer.serializeObject(ele);

                        indexStack.peek().incrementAndGet();
                        waitingForComma.set(true);
                        break;
                    }
                }) //
                .walk();
    }

    private void tryWriteComma(JsonWriter writer, AtomicBoolean waitingForComma) {
        if (waitingForComma.get())
            writer.writeByte(COMMA);
    }

    private void tryWriteWaitingKey(JsonWriter writer, AtomicReference<String> keyRef) {
        if (keyRef.get() == null)
            return;
        writer.writeString(keyRef.get());
        writer.writeByte(SEMI);
        keyRef.set(null);
    }
}
