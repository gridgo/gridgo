package io.gridgo.bean.impl;

import java.util.LinkedList;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.serialize.BSerializer;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unchecked")
class DefaultBArray extends LinkedList<BElement> implements BArray {

	private static final long serialVersionUID = 2037530547593981644L;

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