package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonValue;

import static javax.json.JsonValue.ValueType.STRING;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_A_STRING;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;

public class JsonStringFormat implements JsonFormat<String> {

    @Override
    public JsonValue write(String toWrite) {
        return Json.createValue(toWrite);
    }

    @Override
    public String read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            ctx.notifyFailure(UNEXPECTED_NULL.getErrorMessageKey());
            return null;
        }

        if (toRead.getValueType() != STRING) {
            ctx.notifyFailure(NOT_A_STRING.getErrorMessageKey(), toRead.getValueType());
            return null;
        }

        JsonString jsonString = (JsonString) toRead;

        return jsonString.getString();
    }
}
