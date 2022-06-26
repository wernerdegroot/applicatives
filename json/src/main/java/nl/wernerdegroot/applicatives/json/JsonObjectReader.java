package nl.wernerdegroot.applicatives.json;

import javax.json.JsonObject;
import javax.json.JsonValue;

import static javax.json.JsonValue.ValueType.OBJECT;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_AN_OBJECT;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;

public interface JsonObjectReader<T> extends JsonReader<T> {

    T read(JsonObject toRead, ValidationContext ctx);

    @Override
    default T read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            ctx.notifyFailure(UNEXPECTED_NULL.getErrorMessageKey());
            return null;
        }

        if (toRead.getValueType() != OBJECT) {
            ctx.notifyFailure(NOT_AN_OBJECT.getErrorMessageKey(), toRead.getValueType());
            return null;
        }

        JsonObject jsonObject = (JsonObject) toRead;

        return read(jsonObject, ctx);
    }
}
