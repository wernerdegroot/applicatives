package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.Optional;

import static javax.json.JsonValue.ValueType.NULL;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public class JsonOptionalFormat<T> implements JsonFormat<Optional<T>> {

    private final JsonFormat<T> innerFormat;

    public JsonOptionalFormat(JsonFormat<T> innerFormat) {
        this.innerFormat = innerFormat;
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
        T inner = innerFormat.read(toRead, ctx);
        return ctx.finishReading() == SUCCESS ? Optional.of(inner) : null;
    }

    @Override
    public JsonValue write(Optional<T> toWrite) {
        return toWrite.map(innerFormat::write).orElse(null);
    }
}
