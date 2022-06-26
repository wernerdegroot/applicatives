package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.List;
import java.util.function.Function;

public interface JsonReader<T> {

    Json.Result<T> read(Path path, JsonValue toRead);

    default JsonReader<List<T>> list() {
        return new JsonListReader<>(this);
    }

    default <U> JsonReader<U> validate(Function<? super T, ? extends Validated<U>> validation) {
        return (path, toRead) -> read(path, toRead).validate(path, validation);
    }

    default Json.Result<T> read(JsonValue toRead) {
        return read(Path.empty(), toRead);
    }
}
