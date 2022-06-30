package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.Optional;

import static javax.json.JsonValue.ValueType.NULL;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public class JsonOptionalReader<T> implements JsonReader<Optional<T>> {

    private final JsonReader<T> innerReader;

    public JsonOptionalReader(JsonReader<T> innerReader) {
        this.innerReader = innerReader;
    }

    @Override
    public Optional<T> read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            return Optional.empty();
        }

        if (toRead.getValueType() == NULL) {
            return Optional.empty();
        }

        ctx.startReading();
        T inner = innerReader.read(toRead, ctx);
        return ctx.finishReading() == SUCCESS ? Optional.of(inner) : null;
    }
}
