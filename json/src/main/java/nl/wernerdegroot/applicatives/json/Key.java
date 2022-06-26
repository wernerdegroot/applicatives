package nl.wernerdegroot.applicatives.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import static nl.wernerdegroot.applicatives.json.Json.*;

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

    public <T> JsonObjectReader<T> readUsing(JsonReader<T> reader) {
        return (path, toRead) -> reader.read(path.append(key), toRead.get(key));
    }

    public JsonObjectReader<String> readString() {
        return readUsing(STRING);
    }

    public JsonObjectReader<Integer> readInt() {
        return readUsing(INT);
    }

    public JsonObjectReader<Long> readLong() {
        return readUsing(LONG);
    }

    public JsonObjectReader<Double> readDouble() {
        return readUsing(DOUBLE);
    }

    public JsonObjectReader<BigInteger> readBigInteger() {
        return readUsing(BIG_INTEGER);
    }

    public JsonObjectReader<BigDecimal> readBigDecimal() {
        return readUsing(BIG_DECIMAL);
    }

    public <T> JsonObjectWriter<T> writeUsing(JsonWriter<T> writer) {
        return (builder, toWrite) -> builder.add(key, writer.write(toWrite));
    }

    public JsonObjectWriter<String> writeString() {
        return writeUsing(STRING);
    }

    public JsonObjectWriter<Integer> writeInt() {
        return writeUsing(INT);
    }

    public JsonObjectWriter<Long> writeLong() {
        return writeUsing(LONG);
    }

    public JsonObjectWriter<Double> writeDouble() {
        return writeUsing(DOUBLE);
    }

    public JsonObjectWriter<BigDecimal> writeBigDecimal() {
        return writeUsing(BIG_DECIMAL);
    }

    public JsonObjectWriter<BigInteger> writeBigInteger() {
        return writeUsing(BIG_INTEGER);
    }
}
