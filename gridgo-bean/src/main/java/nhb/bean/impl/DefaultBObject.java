package nhb.bean.impl;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import nhb.bean.BElement;
import nhb.bean.BObject;
import nhb.bean.serialize.BSerializer;

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
