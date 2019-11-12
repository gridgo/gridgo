package io.gridgo.bean.serialization.json;

import com.dslplatform.json.JsonReader.ReadObject;
import com.dslplatform.json.JsonWriter.WriteObject;

public interface ReadWriteObject<T> extends ReadObject<T>, WriteObject<T> {

}
