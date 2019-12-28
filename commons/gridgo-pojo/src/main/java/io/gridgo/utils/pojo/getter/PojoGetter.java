package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.getter.fieldwalkers.FieldWalker;
import io.gridgo.utils.pojo.getter.fieldwalkers.GenericFieldWalker;
import lombok.NonNull;

public class PojoGetter {

    public static PojoGetter of(@NonNull Object target, PojoGetterProxy proxy) {
        return new PojoGetter(target, proxy);
    }

    public static PojoGetter of(@NonNull Object target) {
        return of(target, null);
    }

    /********************************************************
     ********************* END OF STATIC ********************
     ********************************************************/

    private final FieldWalker fieldWalker = GenericFieldWalker.getInstance();

    private final Object target;

    private final PojoGetterProxy proxy;

    private boolean shallowly = false;

    @NonNull
    private PojoFlattenAcceptor walker;

    private PojoGetter(Object target, PojoGetterProxy proxy) {
        this.target = target;
        this.proxy = proxy;
    }

    public PojoGetter shallowly(boolean shallowly) {
        this.shallowly = shallowly;
        return this;
    }

    public PojoGetter walker(PojoFlattenAcceptor walker) {
        this.walker = walker;
        return this;
    }

    public void walk() {
        fieldWalker.walk(target, proxy, walker, shallowly);
    }
}
