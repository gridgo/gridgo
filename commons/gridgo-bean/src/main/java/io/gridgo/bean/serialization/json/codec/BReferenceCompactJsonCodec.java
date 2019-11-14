package io.gridgo.bean.serialization.json.codec;

import static com.dslplatform.json.JsonWriter.ARRAY_END;
import static com.dslplatform.json.JsonWriter.ARRAY_START;
import static com.dslplatform.json.JsonWriter.COMMA;
import static com.dslplatform.json.JsonWriter.OBJECT_END;
import static com.dslplatform.json.JsonWriter.OBJECT_START;
import static com.dslplatform.json.JsonWriter.SEMI;
import static io.gridgo.utils.pojo.PojoUtils.walkThroughGetter;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BReference;
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
        var alreadyWriteValue = new AtomicBoolean(false);

        walkThroughGetter(reference, (indicator, val) -> {
            switch (indicator) {
            case START_MAP:
                tryWriteComma(writer, alreadyWriteValue);
                tryWriteWaitingKey(writer, keyRef);
                writer.writeByte(OBJECT_START);

                alreadyWriteValue.set(false);
                lengthStack.push((int) val);
                indexStack.push(new AtomicInteger(0));

                if (indexStack.size() > 0)
                    indexStack.peek().incrementAndGet();

                break;
            case END_MAP:
                indexStack.pop();
                lengthStack.pop();
                writer.writeByte(OBJECT_END);
                alreadyWriteValue.set(true);
                break;
            case START_ARRAY:
                tryWriteComma(writer, alreadyWriteValue);
                tryWriteWaitingKey(writer, keyRef);
                writer.writeByte(ARRAY_START);

                alreadyWriteValue.set(false);
                lengthStack.push((int) val);
                indexStack.push(new AtomicInteger(0));

                if (indexStack.size() > 0)
                    indexStack.peek().incrementAndGet();

                break;
            case END_ARRAY:
                indexStack.pop();
                lengthStack.pop();
                writer.writeByte(ARRAY_END);
                alreadyWriteValue.set(true);
                break;
            case KEY:
                keyRef.set((String) val);
                break;
            case KEY_NULL:
                indexStack.peek().incrementAndGet();
                break;
            case VALUE:
                tryWriteComma(writer, alreadyWriteValue);
                tryWriteWaitingKey(writer, keyRef);
                writer.serializeObject(val);
                indexStack.peek().incrementAndGet();
                alreadyWriteValue.set(true);
                break;
            }
        });
    }

    private void tryWriteComma(JsonWriter writer, AtomicBoolean alreadyWriteValue) {
        if (alreadyWriteValue.get())
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
