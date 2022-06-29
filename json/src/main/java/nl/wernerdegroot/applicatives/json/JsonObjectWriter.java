package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * For writing JSON objects (as opposed to other JSON values).
 */
public interface JsonObjectWriter<T> extends JsonWriter<T> {

    void write(JsonObjectBuilder builder, T toWrite);

    default <U> JsonObjectWriter<U> withValue(T value) {
        return (builder, ignored) -> write(builder, value);
    }

    default JsonValue write(T toWrite) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        write(builder, toWrite);
        return builder.build();
    }

    default JsonObjectWriter<T> combineWith(JsonObjectWriter<? super T> that) {
        return (builder, toWrite) -> {
            this.write(builder, toWrite);
            that.write(builder, toWrite);
        };
    }
}