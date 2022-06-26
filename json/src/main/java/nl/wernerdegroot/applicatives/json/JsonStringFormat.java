package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.STRING;
import static nl.wernerdegroot.applicatives.json.Errors.*;
import static nl.wernerdegroot.applicatives.json.Json.failed;
import static nl.wernerdegroot.applicatives.json.Json.success;
import static nl.wernerdegroot.applicatives.json.Json.Result;

public class JsonStringFormat implements JsonFormat<String > {

    @Override
    public JsonValue write(String  toWrite) {
        return Json.createValue(toWrite);
    }

    @Override
    public Result<String > read(Path path, JsonValue toRead) {
        if (toRead == null) {
            return failed(path, UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != STRING) {
            return failed(path, NOT_A_STRING.getErrorMessageKey(), toRead.getValueType());
        }

        JsonString jsonString = (JsonString) toRead;

        return success(jsonString.getString());
    }
}
