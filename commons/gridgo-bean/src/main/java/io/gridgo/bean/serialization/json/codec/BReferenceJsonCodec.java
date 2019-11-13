package io.gridgo.bean.serialization.json.codec;

import static com.dslplatform.json.JsonWriter.ARRAY_END;
import static com.dslplatform.json.JsonWriter.ARRAY_START;
import static com.dslplatform.json.JsonWriter.COMMA;
import static com.dslplatform.json.JsonWriter.OBJECT_END;
import static com.dslplatform.json.JsonWriter.OBJECT_START;
import static com.dslplatform.json.JsonWriter.SEMI;
import static io.gridgo.utils.pojo.PojoUtils.walkThroughGetter;

import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BReferenceJsonCodec implements JsonCodec<BReference> {

    @NonNull
    private final BElementJsonCodec compositeCodec;

    @Override
    public void write(JsonWriter writer, BReference value) {
        Object reference;
        if (value == null || (reference = value.getReference()) == null) {
            writer.writeNull();
            return;
        }

        var lengthStack = new Stack<Integer>();
        var indexStack = new Stack<AtomicInteger>();

        walkThroughGetter(reference, (indicator, val) -> {
            switch (indicator) {
            case START_MAP:
                if (indexStack.size() > 0)
                    indexStack.peek().incrementAndGet();
                lengthStack.push((int) val);
                indexStack.push(new AtomicInteger(0));
                writer.writeByte(OBJECT_START);
                break;
            case END_MAP:
                indexStack.pop();
                lengthStack.pop();
                writer.writeByte(OBJECT_END);
                if (lengthStack.size() > 0 && lengthStack.peek() > indexStack.peek().get())
                    writer.writeByte(COMMA);
                break;
            case START_ARRAY:
                if (indexStack.size() > 0)
                    indexStack.peek().incrementAndGet();
                lengthStack.push((int) val);
                indexStack.push(new AtomicInteger(0));
                writer.writeByte(ARRAY_START);
                break;
            case END_ARRAY:
                indexStack.pop();
                lengthStack.pop();
                writer.writeByte(ARRAY_END);
                if (lengthStack.size() > 0 && lengthStack.peek() > indexStack.peek().get())
                    writer.writeByte(COMMA);
                break;
            case KEY:
                writer.writeString((String) val);
                writer.writeByte(SEMI);
                break;
            case VALUE:
                compositeCodec.write(writer, BElement.wrapAny(val));
                if (lengthStack.size() > 0 && indexStack.peek().incrementAndGet() < lengthStack.peek())
                    writer.writeByte(COMMA);
                break;
            }
        });
    }

    @Override
    public BReference read(JsonReader reader) throws IOException {
        return null;
    }
}
