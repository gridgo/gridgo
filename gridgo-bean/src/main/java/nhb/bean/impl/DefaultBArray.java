package nhb.bean.impl;

import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import nhb.bean.BArray;
import nhb.bean.BElement;
import nhb.bean.serialize.BSerializer;

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