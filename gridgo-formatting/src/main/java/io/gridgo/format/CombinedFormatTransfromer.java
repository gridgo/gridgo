package io.gridgo.format;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

class CombinedFormatTransfromer implements FormatTransformer {

	@Getter
	private final List<FormatTransformer> chain = new LinkedList<>();

	@Override
	public final Object transform(Object source) {
		Object result = source;
		for (FormatTransformer transformer : chain) {
			result = transformer.transform(result);
		}
		return result;
	}
}
