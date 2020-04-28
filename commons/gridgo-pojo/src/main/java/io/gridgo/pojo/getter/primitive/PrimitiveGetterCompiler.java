package io.gridgo.pojo.getter.primitive;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.tuple.Pair;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.pojo.getter.AbstractGetterCompiler;
import io.gridgo.pojo.getter.PojoGetter;
import io.gridgo.pojo.getter.primitive.array.BooleanArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.CharArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.DoubleArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.FloatArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.IntArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.LongArrayPrimitiveGetter;
import io.gridgo.pojo.getter.primitive.array.ShortArrayPrimitiveGetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitiveGetterCompiler extends AbstractGetterCompiler {

    @Getter
    private static final PrimitiveGetterCompiler instance = new PrimitiveGetterCompiler();

    private Pair<Class<? extends PojoPrimitiveGetter>, String> getInterface(Class<?> type) {
        var isArray = type.isArray();
        var primitiveType = PrimitiveType.valueOf(isArray ? type.getComponentType() : type);
        if (primitiveType == null)
            return null;

        switch (primitiveType) {
        case BOOLEAN:
            return isArray //
                    ? Pair.of(BooleanArrayPrimitiveGetter.class, "getBooleanArray") //
                    : Pair.of(BooleanPrimitiveGetter.class, "getBooleanValue");
        case INT:
            return isArray //
                    ? Pair.of(IntArrayPrimitiveGetter.class, "getIntArray") //
                    : Pair.of(IntPrimitiveGetter.class, "getIntValue");
        case LONG:
            return isArray //
                    ? Pair.of(LongArrayPrimitiveGetter.class, "getLongArray") //
                    : Pair.of(LongPrimitiveGetter.class, "getLongValue");
        case FLOAT:
            return isArray //
                    ? Pair.of(FloatArrayPrimitiveGetter.class, "getFloatArray") //
                    : Pair.of(FloatPrimitiveGetter.class, "getFloatValue");
        case DOUBLE:
            return isArray //
                    ? Pair.of(DoubleArrayPrimitiveGetter.class, "getDoubleArray") //
                    : Pair.of(DoublePrimitiveGetter.class, "getDoubleValue");
        case BYTE:
            return isArray //
                    ? Pair.of(BytePrimitiveGetter.class, "getByteValue") //
                    : Pair.of(BytePrimitiveGetter.class, "getByteValue");
        case SHORT:
            return isArray //
                    ? Pair.of(ShortArrayPrimitiveGetter.class, "getShortArray") //
                    : Pair.of(ShortPrimitiveGetter.class, "getShortValue");
        case CHAR:
            return isArray //
                    ? Pair.of(CharArrayPrimitiveGetter.class, "getCharArray") //
                    : Pair.of(CharPrimitiveGetter.class, "getCharValue");
        default:
            return null;
        }

    }

    @Override
    public PojoGetter compile(Method method) {
        var theInterface = getInterface(method.getReturnType());
        if (theInterface == null)
            throw new IllegalArgumentException(
                    "Method must return primitive type: " + method.getDeclaringClass() + "." + method.getName());
        var classSrc = buildImplClass(method, theInterface.getLeft(), theInterface.getRight());
        return doCompile(classSrc.getName(), classSrc.toString());
    }

    @Override
    public PojoGetter compile(Field field) {
        var theInterface = getInterface(field.getType());
        if (theInterface == null)
            throw new IllegalArgumentException(
                    "Field should be primitive: " + field.getDeclaringClass() + "." + field.getName());
        var classSrc = buildImplClass(field, theInterface.getLeft(), theInterface.getRight());
        return doCompile(classSrc.getName(), classSrc.toString());
    }
}
