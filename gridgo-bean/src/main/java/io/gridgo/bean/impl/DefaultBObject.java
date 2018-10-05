package io.gridgo.bean.impl;

import java.util.HashMap;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.serialize.BSerializer;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unchecked")
class DefaultBObject extends HashMap<String, BElement> implements BObject {

	private static final long serialVersionUID = -782587140021900238L;

	@Setter
	@Getter
	private transient BFactory factory = BFactory.DEFAULT;

	@Setter
	@Getter
	private transient BSerializer serializer;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.writeString(null, 0, sb);
		return sb.toString();
	}
}
