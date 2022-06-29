package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A combination of a `JsonReader` and a `JsonWriter`, for when you
 * want to serialize the same way as you deserialize (which is most
 * of the time).
 */
public interface JsonFormat<T> extends JsonReader<T>, JsonWriter<T> {

    static <T> JsonFormat<T> of(JsonReader<? extends T> reader, JsonWriter<? super T> writer) {
        return new JsonFormat<T>() {
            @Override
            public T read(JsonValue toRead, ValidationContext ctx) {
                return reader.read(toRead, ctx);
            }

            @Override
            public JsonValue write(T toWrite) {
                return writer.write(toWrite);
            }
        };
    }

    @Override
    default JsonFormat<List<T>> list() {
        return of(JsonReader.super.list(), JsonWriter.super.list());
    }

    default JsonFormat<T> verify(Verification<T> verification) {
        return JsonFormat.of(
                JsonReader.super.verify(verification),
                this
        );
    }

    default JsonFormat<Optional<T>> optional() {
        return new JsonOptionalFormat<>(this);
    }

    default <U> JsonFormat<U> inmap(Function<? super T, ? extends U> fn, Function<? super U, ? extends T> gn) {
        return of(map(fn), contramap(gn));
    }
}
