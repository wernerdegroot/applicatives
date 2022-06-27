package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

import static javax.json.Json.createReader;
import static nl.wernerdegroot.applicatives.json.Json.failed;
import static nl.wernerdegroot.applicatives.json.Json.success;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

public interface JsonReader<T> {

    static <T> JsonReader<T> fail(String errorMessageKey, Object... arguments) {
        return (toRead, ctx) -> {
            return ctx.notifyFailure(errorMessageKey, arguments);
        };
    }

    T read(JsonValue toRead, ValidationContext ctx);

    default JsonReader<List<T>> list() {
        return new JsonListReader<>(this);
    }

    default <U> JsonReader<U> map(Function<? super T, ? extends U> fn) {
        return (toRead, ctx) -> fn.apply(read(toRead, ctx));
    }

    default <U> JsonReader<U> flatMap(Function<? super T, ? extends JsonReader<? extends U>> fn) {
        return (toRead, ctx) -> fn.apply(read(toRead, ctx)).read(toRead, ctx);
    }

    default <U> JsonReader<U> validate(Validation<? super T, ? extends U> validation) {
        return (toRead, ctx) -> {
            ctx.startReading();
            T result = read(toRead, ctx);
            return ctx.finishReading() == SUCCESS ? validation.validate(result, ctx) : null;
        };
    }

    default Json.Result<T> readString(String toRead) {
        ValidationContext validationContext = new ValidationContext();
        validationContext.startReading();
        T result = read(createReader(new StringReader(toRead)).readValue(), validationContext);
        return validationContext.finishReading() == SUCCESS ? success(result) : failed(validationContext.getFailures());
    }
}
