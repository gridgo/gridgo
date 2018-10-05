package io.gridgo.bean.serialize;

public interface BSerializerAware {

	void setSerializer(BSerializer serializer);

	BSerializer getSerializer();
}
