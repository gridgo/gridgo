package io.gridgo.format;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface FormatTransformerRegistry {

	static FormatTransformerRegistry newInstance(FormatTransformerRegistry... registrys) {
		FormatTransformerRegistry result = new DefaultFormatTransformerRegistry();
		if (registrys != null) {
			for (FormatTransformerRegistry registry : registrys) {
				result.inherit(registry);
			}
		}
		return result;
	}

	/**
	 * Create new FormatTransformerRegistry, inherited from
	 * CommonTextTransformerRegistry, CommonNumberTransformerRegistry,
	 * CommonDateTransformerRegistry
	 * 
	 * @return
	 */
	static FormatTransformerRegistry newwDefault() {
		return newInstance(//
				CommonTextTransformerRegistry.newInstance(), //
				CommonNumberTransformerRegistry.newInstance(), //
				CommonDateTransformerRegistry.newInstance());
	}

	default List<FormatTransformer> getChain(String... transformerNames) {
		return this.getChain(Arrays.asList(transformerNames));
	}

	Map<String, FormatTransformer> getAll();

	List<FormatTransformer> getChain(List<String> transformerNames);

	FormatTransformer addTransformer(String name, FormatTransformer transformer);

	default FormatTransformer addAlias(String name, String... chain) {
		if (name == null) {
			throw new NullPointerException("Name cannot be null");
		}
		if (chain != null && chain.length > 0) {
			CombinedFormatTransfromer combinedFormatTransformer = new CombinedFormatTransfromer();
			List<String> list = new LinkedList<>();
			for (String str : chain) {
				if (str != null) {
					String[] arr = str.trim().split("\\s*>\\s*");
					for (String n : arr) {
						if (n.trim().length() > 0) {
							list.add(n);
						}
					}
				}
			}
			combinedFormatTransformer.getChain().addAll(this.getChain(list));
			return this.addTransformer(name, combinedFormatTransformer);
		}
		return null;
	}

	FormatTransformer removeTransformer(String name);

	default FormatTransformerRegistry inherit(FormatTransformerRegistry parent) {
		if (parent != null) {
			for (Entry<String, FormatTransformer> entry : parent.getAll().entrySet()) {
				this.addTransformer(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}
}
