package io.gridgo.pojo.setter.primitive;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.tuple.Pair;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.pojo.setter.AbstractSetterCompiler;
import io.gridgo.pojo.setter.PojoSetter;
import io.gridgo.pojo.setter.primitive.array.BooleanArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.CharArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.DoubleArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.FloatArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.IntArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.LongArrayPrimitiveSetter;
import io.gridgo.pojo.setter.primitive.array.ShortArrayPrimitiveSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitiveSetterCompiler extends AbstractSetterCompiler {

    @Getter
    private static final PrimitiveSetterCompiler instance = new PrimitiveSetterCompiler();

    private Pair<Class<? extends PojoPrimitiveSetter>, String> getInterface(Class<?> type) {
        var isArray = type.isArray();
        var primitiveType = PrimitiveType.valueOf(isArray ? type.getComponentType() : type);
        if (primitiveType == null)
            return null;

        switch (primitiveType) {
        case BOOLEAN:
            return isArray //
                    ? Pair.of(BooleanArrayPrimitiveSetter.class, "setBooleanArray") //
                    : Pair.of(BooleanPrimitiveSetter.class, "setBooleanValue") //
            ;
        case BYTE:
            return isArray //
                    ? Pair.of(BytePrimitiveSetter.class, "setByteArray") //
                    : Pair.of(BytePrimitiveSetter.class, "setByteValue");
        case CHAR:
            return isArray //
                    ? Pair.of(CharArrayPrimitiveSetter.class, "setCharArray") //
                    : Pair.of(CharPrimitiveSetter.class, "setCharValue");
        case DOUBLE:
            return isArray //
                    ? Pair.of(DoubleArrayPrimitiveSetter.class, "setDoubleArray") //
                    : Pair.of(DoublePrimitiveSetter.class, "setDoubleValue");
        case FLOAT:
            return isArray //
                    ? Pair.of(FloatArrayPrimitiveSetter.class, "setFloatArray") //
                    : Pair.of(FloatPrimitiveSetter.class, "setFloatValue");
        case INT:
            return isArray //
                    ? Pair.of(IntArrayPrimitiveSetter.class, "setIntArray") //
                    : Pair.of(IntPrimitiveSetter.class, "setIntValue");
        case LONG:
            return isArray //
                    ? Pair.of(LongArrayPrimitiveSetter.class, "setLongArray") //
                    : Pair.of(LongPrimitiveSetter.class, "setLongValue");
        case SHORT:
            return isArray //
                    ? Pair.of(ShortArrayPrimitiveSetter.class, "setShortArray") //
                    : Pair.of(ShortPrimitiveSetter.class, "setShortValue");
        default:
            return null;
        }

    }

    @Override
    public PojoSetter compile(Method method) {
        var theInterface = getInterface(method.getParameters()[0].getType());
        if (theInterface == null)
            throw new IllegalArgumentException("Method must accept only 1 primitive argument: "
                    + method.getDeclaringClass() + "." + method.getName());
        var classSrc = buildImplClass(method, theInterface.getLeft(), theInterface.getRight());
        return doCompile(classSrc.getName(), classSrc.toString());
    }

    @Override
    public PojoSetter compile(Field field) {
        var theInterface = getInterface(field.getType());
        if (theInterface == null)
            throw new IllegalArgumentException(
                    "Field should be primitive: " + field.getDeclaringClass() + "." + field.getName());
        var classSrc = buildImplClass(field, theInterface.getLeft(), theInterface.getRight());
        return doCompile(classSrc.getName(), classSrc.toString());
    }

}
