package io.gridgo.pojo;

import io.gridgo.pojo.input.PojoSchemaInput;
import io.gridgo.pojo.output.PojoSchemaOutput;

public interface PojoSchema<T> {

    T newInstance();

    void serialize(T target, PojoSchemaOutput output);

    void deserialize(T target, PojoSchemaInput input);

    default T deserialize(PojoSchemaInput input) {
        var t = newInstance();
        deserialize(t, input);
        return t;
    }
}
