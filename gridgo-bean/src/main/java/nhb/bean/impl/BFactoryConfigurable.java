package nhb.bean.impl;

import java.util.function.Supplier;

import nhb.bean.BArray;
import nhb.bean.BObject;
import nhb.bean.BValue;
import nhb.bean.serialize.BSerializer;
import nhb.bean.xml.BXmlParser;

public interface BFactoryConfigurable {

	void setValueSupplier(Supplier<BValue> valueSupplier);

	void setObjectSupplier(Supplier<BObject> objectSupplier);

	void setArraySupplier(Supplier<BArray> arraySupplier);

	void setXmlParser(BXmlParser parser);

	void setSerializer(BSerializer serializer);
}