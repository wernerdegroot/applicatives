package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonValue;

import java.math.BigDecimal;

import static javax.json.JsonValue.ValueType.NUMBER;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_A_NUMBER;
import static nl.wernerdegroot.applicatives.json.Json.failed;
import static nl.wernerdegroot.applicatives.json.Json.success;
import static nl.wernerdegroot.applicatives.json.Json.Result;

public class JsonBigDecimalFormat implements JsonFormat<BigDecimal> {

    @Override
    public JsonValue write(BigDecimal toWrite) {
        return Json.createValue(toWrite);
    }

    @Override
    public Result<BigDecimal> read(Path path, JsonValue toRead) {
        if (toRead == null) {
            return failed(path, UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != NUMBER) {
            return failed(path, NOT_A_NUMBER.getErrorMessageKey(), toRead.getValueType());
        }

        JsonNumber jsonNumber = (JsonNumber) toRead;

        return success(jsonNumber.bigDecimalValue());
    }
}
