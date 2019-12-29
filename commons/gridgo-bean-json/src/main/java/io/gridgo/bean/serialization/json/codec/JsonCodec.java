package io.gridgo.bean.serialization.json.codec;

import com.dslplatform.json.JsonReader.ReadObject;
import com.dslplatform.json.JsonWriter.WriteObject;

public interface JsonCodec<T> extends ReadObject<T>, WriteObject<T> {

}
