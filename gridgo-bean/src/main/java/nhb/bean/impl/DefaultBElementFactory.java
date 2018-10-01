package nhb.bean.impl;

import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;
import nhb.bean.BArray;
import nhb.bean.BObject;
import nhb.bean.BValue;
import nhb.bean.serialize.BSerializer;
import nhb.bean.serialize.msgpack.MsgpackSerializer;
import nhb.bean.xml.BXmlParser;

class DefaultBElementFactory implements BFactory, BFactoryConfigurable {

	@Setter
	@Getter
	private Supplier<BArray> arraySupplier = DefaultBArray::new;

	@Getter
	@Setter
	private Supplier<BObject> objectSupplier = DefaultBObject::new;

	@Getter
	@Setter
	private Supplier<BValue> valueSupplier = DefaultBValue::new;

	@Getter
	private BXmlParser xmlParser;

	@Getter
	private BSerializer serializer;

	public DefaultBElementFactory() {
		this.setSerializer(new MsgpackSerializer());
		this.setXmlParser(new BXmlParser());
	}

	@Override
	public void setXmlParser(BXmlParser xmlParser) {
		this.xmlParser = xmlParser;
		if (this.xmlParser instanceof BFactoryAware) {
			this.xmlParser.setFactory(this);
		}
	}

	@Override
	public void setSerializer(BSerializer serializer) {
		this.serializer = serializer;
		if (this.serializer instanceof BFactoryAware) {
			((BFactoryAware) this.serializer).setFactory(this);
		}
	}
}
