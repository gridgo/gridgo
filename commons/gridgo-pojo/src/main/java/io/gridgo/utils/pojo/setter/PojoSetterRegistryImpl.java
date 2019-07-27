package io.gridgo.utils.pojo.setter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.MethodSignatureProxyInjector;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Getter;
import lombok.NonNull;

class PojoSetterRegistryImpl implements PojoSetterRegistry, MethodSignatureProxyInjector {

	@Getter
	private final static PojoSetterRegistryImpl instance = new PojoSetterRegistryImpl();

	private final Map<String, PojoSetterProxy> CACHED_PROXIES = new NonBlockingHashMap<>();

	private final PojoSetterProxyBuilder proxyBuilder = PojoSetterProxyBuilder.newJavassist();

	private PojoSetterRegistryImpl() {
	}

	public PojoSetterProxy getSetterProxy(@NonNull Class<?> type) {
		return CACHED_PROXIES.computeIfAbsent(type.getName(), k -> buildProxy(type));
	}

	private PojoSetterProxy buildProxy(Class<?> type) {
		PojoSetterProxy proxy = proxyBuilder.buildSetterProxy(type);
		for (PojoMethodSignature signature : proxy.getSignatures()) {
			try {
				setProxyForMethod(signature);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return proxy;
	}

	private void setProxyForMethod(PojoMethodSignature signature) {
		if (PojoUtils.isSupported(signature.getFieldType())) {
			setSetterProxy(signature, getSetterProxy(signature.getFieldType()));
		} else {
			setSetterProxyForUnsupportedTypes(signature);
		}
	}

	private void setSetterProxyForUnsupportedTypes(PojoMethodSignature signature) {
		Class<?> elementType = PojoUtils.getElementTypeForGeneric(signature);
		if (elementType != null && PojoUtils.isSupported(elementType)) {
			setElementSetterProxy(signature, getSetterProxy(elementType));
		}
	}
}
