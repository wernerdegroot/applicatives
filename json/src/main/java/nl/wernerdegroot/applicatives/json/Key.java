package nl.wernerdegroot.applicatives.json;

import javax.json.JsonValue;

public class Key {

    private final String key;

    public Key(String key) {
        this.key = key;
    }

    public static Key of(String key) {
        return new Key(key);
    }

    public static Key key(String key) {
        return new Key(key);
    }

    public <T> JsonObjectReader<T> using(JsonReader<T> reader) {
        return readUsing(reader);
    }

    public <T> JsonObjectReader<T> readUsing(JsonReader<T> reader) {
        return (toRead, ctx) -> {
            ctx.pushKey(key);
            T result = reader.read(toRead.get(key), ctx);
            ctx.popKey();
            return result;
        };
    }

    public <T> JsonObjectWriter<T> using(JsonWriter<T> writer) {
        return writeUsing(writer);
    }

    public <T> JsonObjectWriter<T> writeUsing(JsonWriter<T> writer) {
        return (builder, toWrite) -> {
            JsonValue written = writer.write(toWrite);
            if (written != null) {
                builder.add(key, writer.write(toWrite));
            }
        };
    }

    public <T> JsonObjectFormat<T> using(JsonFormat<T> format) {
        return formatUsing(format);
    }

    public <T> JsonObjectFormat<T> formatUsing(JsonFormat<T> format) {
        return JsonObjectFormat.of(readUsing(format), writeUsing(format));
    }
}
