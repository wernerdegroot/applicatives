package nl.wernerdegroot.applicatives.json;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface JsonObjectWriter<T> extends JsonWriter<T> {

    void write(JsonObjectBuilder builder, T toWrite);

    default JsonValue write(T toWrite) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        write(builder, toWrite);
        return builder.build();
    }

    default String writeString(T toWrite) {
        return write(toWrite).toString();
    }

}