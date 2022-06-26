package nl.wernerdegroot.applicatives.json;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

import static javax.json.JsonValue.ValueType.ARRAY;
import static nl.wernerdegroot.applicatives.json.Errors.NOT_AN_ARRAY;
import static nl.wernerdegroot.applicatives.json.Errors.UNEXPECTED_NULL;
import static nl.wernerdegroot.applicatives.json.Json.*;

public class JsonListReader<T> implements JsonReader<List<T>> {

    private final JsonReader<T> elementReader;

    public JsonListReader(JsonReader<T> elementReader) {
        this.elementReader = elementReader;
    }

    @Override
    public Result<List<T>> read(Path path, JsonValue toRead) {
        if (toRead == null) {
            return failed(path, UNEXPECTED_NULL.getErrorMessageKey());
        }

        if (toRead.getValueType() != ARRAY) {
            return failed(path, NOT_AN_ARRAY.getErrorMessageKey(), toRead.getValueType());
        }

        boolean isSuccess = true;
        List<T> successes = new ArrayList<>();
        List<Failure> failures = new ArrayList<>();

        JsonArray jsonArray = (JsonArray) toRead;
        int index = 0;
        for (JsonValue element : jsonArray) {
            Result<T> elementResult = elementReader.read(path, element);
            if (elementResult.isSuccess()) {
                successes.add(elementResult.get());
            } else {
                int indexCopy = index;
                elementResult
                        .getFailures()
                        .stream()
                        .map(failure -> failure.atPathComponent(Integer.toString(indexCopy)))
                        .forEachOrdered(failures::add);
                isSuccess = false;
            }
            index++;
        }

        return isSuccess
                ? success(successes)
                : failed(failures);
    }
}
