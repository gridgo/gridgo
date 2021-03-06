package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.codec.BElementJsonCodec.COMPACT;
import static io.gridgo.bean.serialization.json.codec.BElementJsonCodec.NO_COMPACT;

import java.io.InputStream;
import java.io.OutputStream;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.DslJson.Settings;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractJsonSerializer extends AbstractBSerializer {

    @NonNull
    private final DslJson<Object> dslJson;

    protected AbstractJsonSerializer(@NonNull JsonCompactMode compactMode) {

        var skipNull = compactMode == JsonCompactMode.COMPACT;
        var settings = new Settings<Object>().includeServiceLoader().skipDefaultValues(skipNull);

        dslJson = new DslJson<>(settings);
        dslJson.registerWriter(BElement.class, skipNull ? COMPACT::write : NO_COMPACT::write);
        dslJson.registerReader(BElement.class, skipNull ? COMPACT::read : NO_COMPACT::read);
    }

    @Override
    public void serialize(BElement element, OutputStream out) {
        try {
            if (element.isReference()) {
                var reference = element.asReference().getReference();
                if (reference != null) {
                    var manifiest = reference.getClass();
                    if (reference != null && dslJson.canSerialize(manifiest)) {
                        if (log.isDebugEnabled())
                            log.debug("Manifest '{}' can be serialized by dsl json directly", manifiest);
                        dslJson.serialize(reference, out);
                        return;
                    }
                }
            }

            dslJson.serialize(element, out);
        } catch (Exception e) {
            throw new BeanSerializationException("Cannot serialize element as json", e);
        }
    }

    @Override
    public BElement deserialize(InputStream in) {
        try {
            return dslJson.deserialize(BElement.class, in);
        } catch (Exception e) {
            throw new BeanSerializationException("Cannot deserialize input data", e);
        }
    }
}
