package nl.wernerdegroot.applicatives.json;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.List;

import static javax.json.Json.createReader;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static nl.wernerdegroot.applicatives.json.Errors.*;
import static nl.wernerdegroot.applicatives.json.Json.failed;

public interface JsonObjectReader<T> {

    Json.Result<T> read(Path path, JsonObject toRead);

    default Json.Result<T> readString(String toRead) {
        return read(Path.empty(), createReader(new StringReader(toRead)).readValue());
    }

    default Json.Result<T> read(Path path, JsonValue toRead) {
        if (toRead == null) {
            return failed(path, UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != OBJECT) {
            return failed(path, NOT_AN_OBJECT.getErrorMessageKey(), toRead.getValueType());
        }

        JsonObject jsonObject = (JsonObject) toRead;

        return read(path, jsonObject);
    }

    default JsonReader<T> asReader() {
        return this::read;
    }

    default JsonReader<List<T>> list() {
        return asReader().list();
    }
}
