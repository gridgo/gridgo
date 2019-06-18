package io.gridgo.bean.serialization;

public interface SingleSchemaSerializer<S> extends HasSchemaSerializer {

    void setSchema(Class<? extends S> schema);

    Class<? extends S> getSchema();

    @Override
    default boolean isMulti() {
        return false;
    }
}
