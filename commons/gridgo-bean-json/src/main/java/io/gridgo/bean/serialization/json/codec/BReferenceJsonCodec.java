package io.gridgo.bean.serialization.json.codec;

import static com.dslplatform.json.JsonWriter.ARRAY_END;
import static com.dslplatform.json.JsonWriter.ARRAY_START;
import static com.dslplatform.json.JsonWriter.COMMA;
import static com.dslplatform.json.JsonWriter.OBJECT_END;
import static com.dslplatform.json.JsonWriter.OBJECT_START;
import static com.dslplatform.json.JsonWriter.SEMI;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY_NULL;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.utils.pojo.getter.PojoGetter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class BReferenceJsonCodec implements JsonWriter.WriteObject<BReference> {

    @Override
    public void write(JsonWriter writer, BReference value) {
        Object reference;
        if (value == null || (reference = value.getReference()) == null) {
            writer.writeNull();
            return;
        }

        var lengthStack = new Stack<Integer>();
        var indexStack = new Stack<AtomicInteger>();

        PojoGetter.of(reference, value.getterProxy()) //
                .shallowly(true) //
                .walker((indicator, val, signature, proxy) -> {
                    switch (indicator) {
                    case START_MAP:
                    case START_ARRAY:
                        tryIncreaseTopIndexerOnNewContainer(indexStack);
                        writer.writeByte(indicator == START_ARRAY ? ARRAY_START : OBJECT_START);

                        lengthStack.push((int) val);
                        indexStack.push(new AtomicInteger(0));
                        break;
                    case END_MAP:
                    case END_ARRAY:
                        indexStack.pop();
                        lengthStack.pop();

                        writer.writeByte(indicator == END_ARRAY ? ARRAY_END : OBJECT_END);
                        tryWriteCommaAfterContainerEnd(writer, lengthStack, indexStack);
                        break;
                    case KEY_NULL:
                        if (signature != null && signature.isIgnoreNull()) {
                            indexStack.peek().incrementAndGet();
                            break;
                        }
                    case KEY:
                        writer.writeString((String) val);
                        writer.writeByte(SEMI);

                        if (indicator == KEY_NULL) {
                            writer.writeNull();
                            tryWriteCommaAfterValue(writer, lengthStack, indexStack);
                        }

                        break;
                    case VALUE:
                        var ele = BElement.wrapAny(val);
                        if (val != ele && ele.isReference())
                            ele.asReference().getterProxy(proxy);

                        writer.serializeObject(ele);
                        tryWriteCommaAfterValue(writer, lengthStack, indexStack);
                        break;
                    }
                }).walk();
    }

    private void tryIncreaseTopIndexerOnNewContainer(Stack<AtomicInteger> indexStack) {
        if (indexStack.size() > 0)
            indexStack.peek().incrementAndGet();
    }

    private void tryWriteCommaAfterValue(JsonWriter writer, Stack<Integer> lengthStack,
            Stack<AtomicInteger> indexStack) {
        if (lengthStack.size() > 0 && indexStack.peek().incrementAndGet() < lengthStack.peek())
            writer.writeByte(COMMA);
    }

    private void tryWriteCommaAfterContainerEnd(JsonWriter writer, Stack<Integer> lengthStack,
            Stack<AtomicInteger> indexStack) {
        if (lengthStack.size() > 0 && lengthStack.peek() > indexStack.peek().get())
            writer.writeByte(COMMA);
    }

}
