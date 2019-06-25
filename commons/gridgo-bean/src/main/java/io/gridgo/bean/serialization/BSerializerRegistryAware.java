package io.gridgo.bean.serialization;

import io.gridgo.bean.exceptions.BeanSerializationException;

public interface BSerializerRegistryAware {

    void setSerializerRegistry(BSerializerRegistry serializerRegistry);

    BSerializerRegistry getSerializerRegistry();

    default BSerializer lookupSerializer(String serializerName) {
        var serializer = this.getSerializerRegistry().lookup(serializerName);
        if (serializer == null) {
            throw new BeanSerializationException("Serializer doesn't available for name: " + serializerName);
        }
        return serializer;
    }

    default BSerializer lookupOrDefaultSerializer(String serializerName) {
        var serializer = this.getSerializerRegistry().lookupOrDefault(serializerName);
        if (serializer == null) {
            throw new BeanSerializationException("Serializer doesn't available for name: " + serializerName);
        }
        return serializer;
    }

}
