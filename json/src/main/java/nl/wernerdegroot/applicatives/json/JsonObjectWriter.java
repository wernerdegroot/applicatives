package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.List;

public interface JsonObjectWriter<T> {

    void write(JsonObjectBuilder builder, T toWrite);

    default JsonWriter<List<T>> list() {
        return asWriter().list();
    }

    default JsonValue write(T toWrite) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        write(builder, toWrite);
        return builder.build();
    }

    default String writeString(T toWrite) {
        return write(toWrite).toString();
    }

    default JsonWriter<T> asWriter() {
        return this::write;
    }

}
