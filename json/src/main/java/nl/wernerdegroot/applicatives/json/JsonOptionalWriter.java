package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.Optional;

import static javax.json.JsonValue.ValueType.NULL;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public class JsonOptionalWriter<T> implements JsonWriter<Optional<T>> {

    private final JsonWriter<T> innerWriter;

    public JsonOptionalWriter(JsonWriter<T> innerWriter) {
        this.innerWriter = innerWriter;
    }

    @Override
    public JsonValue write(Optional<T> toWrite) {
        return toWrite.map(innerWriter::write).orElse(null);
    }
}
