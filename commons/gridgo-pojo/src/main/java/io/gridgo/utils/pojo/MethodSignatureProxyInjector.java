package io.gridgo.utils.pojo;

import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;

public interface MethodSignatureProxyInjector {

    default void setGetterProxy(PojoMethodSignature methodSignature, PojoGetterProxy getterProxy) {
        methodSignature.setGetterProxy(getterProxy);
    }

    default void setElementGetterProxy(PojoMethodSignature methodSignature, PojoGetterProxy elementGetterProxy) {
        methodSignature.setElementGetterProxy(elementGetterProxy);
    }

    default void setSetterProxy(PojoMethodSignature methodSignature, PojoSetterProxy setterProxy) {
        methodSignature.setSetterProxy(setterProxy);
    }

    default void setElementSetterProxy(PojoMethodSignature methodSignature, PojoSetterProxy elementSetterProxy) {
        methodSignature.setElementSetterProxy(elementSetterProxy);
    }
}
