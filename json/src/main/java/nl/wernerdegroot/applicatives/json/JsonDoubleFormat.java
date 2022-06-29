package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonValue;

import static javax.json.JsonValue.ValueType.NUMBER;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_A_NUMBER;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;

public class JsonDoubleFormat implements JsonFormat<Double> {

    @Override
    public JsonValue write(Double toWrite) {
        return Json.createValue(toWrite);
    }

    @Override
    public Double read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            return ctx.notifyFailure(UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != NUMBER) {
            return ctx.notifyFailure(NOT_A_NUMBER.getErrorMessageKey(), toRead.getValueType());
        }

        JsonNumber jsonNumber = (JsonNumber) toRead;

        return jsonNumber.doubleValue();
    }
}
