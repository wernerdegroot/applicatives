package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import java.math.BigDecimal;

import static javax.json.JsonValue.ValueType.NUMBER;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_A_NUMBER;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;

public class JsonBigDecimalFormat implements JsonFormat<BigDecimal> {

    @Override
    public JsonValue write(BigDecimal toWrite) {
        return Json.createValue(toWrite);
    }

    @Override
    public BigDecimal read(JsonValue toRead, ValidationContext ctx) {
        if (toRead == null) {
            ctx.notifyFailure(UNEXPECTED_NULL.getErrorMessageKey());
            return null;
        }

        if (toRead.getValueType() != NUMBER) {
            ctx.notifyFailure(NOT_A_NUMBER.getErrorMessageKey(), toRead.getValueType());
            return null;
        }

        JsonNumber jsonNumber = (JsonNumber) toRead;

        return jsonNumber.bigDecimalValue();
    }
}
