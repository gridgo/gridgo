package io.gridgo.bean.impl;

import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialize.BSerializer;
import io.gridgo.utils.PrimitiveUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
class DefaultBValue implements BValue {

	@Setter
	@Getter
	private Object data;

	@Setter
	@Getter
	private transient BSerializer serializer;

	DefaultBValue(Object data) {
		if (data != null && !(data instanceof byte[]) && !PrimitiveUtils.isPrimitive(data.getClass())) {
			throw new InvalidTypeException("Cannot create DefaultBValue from: " + data.getClass() + " instance");
		}
		this.setData(data);
	}

	@Override
	public String toString() {
		return this.getString();
	}
}