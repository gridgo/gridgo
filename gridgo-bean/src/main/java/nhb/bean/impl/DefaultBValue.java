package nhb.bean.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhb.bean.BValue;
import nhb.bean.exceptions.InvalidTypeException;
import nhb.bean.serialize.BSerializer;
import nhb.utils.PrimitiveUtils;

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