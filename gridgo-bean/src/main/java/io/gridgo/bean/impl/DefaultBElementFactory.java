package io.gridgo.bean.impl;

import java.util.function.Supplier;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.bean.serialize.BSerializer;
import io.gridgo.bean.serialize.msgpack.MsgpackSerializer;
import io.gridgo.bean.xml.BXmlParser;
import lombok.Getter;
import lombok.Setter;

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
