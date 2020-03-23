package io.gridgo.bean.serialization.json.codec;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.dslplatform.json.DslJson;

public class TestBElementJsonCodec {

    @Test
    public void testBElementJsonCodec_null() throws IOException {
        DslJson<Object> dsl = new DslJson<Object>();
        var writer = dsl.newWriter();
        try (var output = new ByteArrayOutputStream()) {
            writer.reset(output);
            BElementJsonCodec.COMPACT.write(writer, null);
            writer.flush();
            assertEquals("null", new String(output.toByteArray()));
        }
    }
}
