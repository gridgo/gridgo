package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.BElementJsonSerializer.COMPACT;
import static io.gridgo.bean.serialization.json.BElementJsonSerializer.NO_COMPACT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.DslJson.Settings;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import lombok.NonNull;

public abstract class AbstractJsonSerialzier extends AbstractBSerializer {

    @NonNull
    private final DslJson<BElement> dslJson;

    protected AbstractJsonSerialzier(@NonNull JsonCompactMode compactMode) {

        var omitDefaults = compactMode == JsonCompactMode.COMPACT;
        var settings = new Settings<BElement>() //
                .includeServiceLoader() //
                .skipDefaultValues(omitDefaults);

        dslJson = new DslJson<>(settings);
        dslJson.registerWriter(BElement.class, omitDefaults ? COMPACT::write : NO_COMPACT::write);
        dslJson.registerReader(BElement.class, omitDefaults ? COMPACT::read : NO_COMPACT::read);
    }

    @Override
    public void serialize(BElement element, OutputStream out) {
        try {
            dslJson.serialize(element, out);
        } catch (IOException e) {
            throw new BeanSerializationException("Cannot serialize element as json", e);
        }
    }

    @Override
    public BElement deserialize(InputStream in) {
        try {
            return dslJson.deserialize(BElement.class, in);
        } catch (IOException e) {
            throw new BeanSerializationException("Cannot deserialize input data", e);
        }
    }
}
