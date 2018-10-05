package io.gridgo.format;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

class DefaultFormatTransformerRegistry implements FormatTransformerRegistry {

	private final Map<String, FormatTransformer> registry = new NonBlockingHashMap<>();

	@Override
	public List<FormatTransformer> getChain(List<String> transformerNames) {
		if (transformerNames != null) {
			List<FormatTransformer> list = new LinkedList<>();
			for (String name : transformerNames) {
				if (!registry.containsKey(name)) {
					throw new NullPointerException("StringTransformer for name " + name + " didn't registered");
				}
				list.add(this.registry.get(name));
			}
			return list;
		}
		return null;
	}

	@Override
	public Map<String, FormatTransformer> getAll() {
		return new HashMap<>(this.registry);
	}

	@Override
	public FormatTransformer addTransformer(String name, FormatTransformer transformer) {
		return this.registry.putIfAbsent(name, transformer);
	}

	@Override
	public FormatTransformer removeTransformer(String name) {
		return this.registry.remove(name);
	}
}
