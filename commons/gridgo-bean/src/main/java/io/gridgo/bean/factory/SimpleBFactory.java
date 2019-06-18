package io.gridgo.bean.factory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.impl.MutableBArray;
import io.gridgo.bean.impl.MutableBObject;
import io.gridgo.bean.impl.MutableBReference;
import io.gridgo.bean.impl.MutableBValue;
import io.gridgo.bean.impl.WrappedImmutableBArray;
import io.gridgo.bean.impl.WrappedImmutableBObject;
import io.gridgo.bean.serialization.BSerializerRegistry;
import lombok.Getter;

@Getter
class SimpleBFactory implements BFactory, BFactoryConfigurable {

    private Supplier<BValue> valueSupplier = MutableBValue::new;
    private Supplier<BReference> referenceSupplier = MutableBReference::new;

    private Function<List<BElement>, BArray> arraySupplier = MutableBArray::new;
    private Function<Collection<?>, BArray> wrappedArraySupplier = WrappedImmutableBArray::new;

    private Function<Map<String, BElement>, BObject> objectSupplier = MutableBObject::new;
    private Function<Map<?, ?>, BObject> wrappedObjectSupplier = WrappedImmutableBObject::new;

    private BSerializerRegistry serializerRegistry = new BSerializerRegistry(this);

    @Override
    public BFactoryConfigurable setValueSupplier(Supplier<BValue> valueSupplier) {
        this.valueSupplier = valueSupplier;
        return this;
    }

    @Override
    public BFactoryConfigurable setObjectSupplier(Function<Map<String, BElement>, BObject> objectSupplier) {
        this.objectSupplier = objectSupplier;
        return this;
    }

    @Override
    public BFactoryConfigurable setArraySupplier(Function<List<BElement>, BArray> arraySupplier) {
        this.arraySupplier = arraySupplier;
        return this;
    }

    @Override
    public BFactoryConfigurable setReferenceSupplier(Supplier<BReference> referenceSupplier) {
        this.referenceSupplier = referenceSupplier;
        return this;
    }

    public BFactoryConfigurable setWrappedArraySupplier(Function<Collection<?>, BArray> wrappedArraySupplier) {
        this.wrappedArraySupplier = wrappedArraySupplier;
        return this;
    }

    @Override
    public BFactoryConfigurable setWrappedObjectSupplier(Function<Map<?, ?>, BObject> wrappedObjectSupplier) {
        this.wrappedObjectSupplier = wrappedObjectSupplier;
        return null;
    }

    @Override
    public BFactoryConfigurable asConfigurable() {
        return this;
    }
}
