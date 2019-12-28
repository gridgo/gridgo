package io.gridgo.utils.pojo.getter.fieldwalkers;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY_NULL;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class PojoFieldWalker extends AbstractFieldWalker {

    private static final PojoFieldWalker INSTANCE = new PojoFieldWalker();

    public static PojoFieldWalker getInstance() {
        return INSTANCE;
    }

    private PojoFieldWalker() {
        // Nothing to do
    }

    @SuppressWarnings("unchecked")
    @Override
    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        int length = proxy.getFields().length;
        walker.accept(START_MAP, length, null, null);
        proxy.walkThrough(target, (signature, value) -> {
            var key = signature.getTransformedOrDefaultFieldName();

            var valueTranslator = signature.getValueTranslator();
            if (valueTranslator != null && valueTranslator.translatable(value))
                value = valueTranslator.translate(value, signature);

            if (value == null) {
                walker.accept(KEY_NULL, key, signature, null);
            } else {
                walker.accept(KEY, key, null, null);
                if (shallowly) {
                    walker.accept(VALUE, value, signature, signature.getGetterProxy());
                } else {
                    walkRecursive(value, signature.getGetterProxy(), walker, shallowly);
                }
            }
        });
        walker.accept(END_MAP, length, null, null);
    }
}
