package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.io.StringReader;
import java.util.List;

import static javax.json.Json.createReader;
import static nl.wernerdegroot.applicatives.json.Json.failed;
import static nl.wernerdegroot.applicatives.json.Json.success;

public interface JsonReader<T> {

    T read(JsonValue toRead, ValidationContext ctx);

    default JsonReader<List<T>> list() {
        return new JsonListReader<>(this);
    }

    default <U> JsonReader<U> validate(Validation<? super T, ? extends U> validation) {
        return (toRead, ctx) -> {
            ctx.startReading();
            T result = read(toRead, ctx);
            if (!ctx.finishReading()) {
                return null;
            } else {
                return validation.validate(result, ctx);
            }
        };
    }

    default Json.Result<T> readString(String toRead) {
        ValidationContext validationContext = new ValidationContext();
        validationContext.startReading();
        T result = read(createReader(new StringReader(toRead)).readValue(), validationContext);
        if (validationContext.finishReading()) {
            return success(result);
        } else {
            return failed(validationContext.getFailures());
        }
    }
}
