package com.lemnik.minijson;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class JsonDecoder {

    /*
     * We keep a single shared StringBuilder that we simply reset each time we need to read a String literal. Since
     * we never read nested String literals - this is completely safe, although definitely goes against most ideas
     * of "best practice" - it's also really fast.
     */
    private final StringBuilder sharedStringBuilder = new StringBuilder();

    protected final PushbackReader reader;

    public JsonDecoder(Reader reader) {
        this.reader = new PushbackReader(Objects.requireNonNull(reader, "reader"));
    }

    public JsonDecoder(String json) {
        this.reader = new PushbackReader(new StringReader(Objects.requireNonNull(json, "json")));
    }

    public final Object nextJsonValue() throws IOException {
        int codePoint = nextSignificantCodepoint();
        Object value = readJsonValue(codePoint);
        if (value == null) {
            throw new IOException("parse error");
        }

        return value;
    }

    private Map<String, Object> readObject() throws IOException {
        int codePoint = nextSignificantCodepoint();
        if (codePoint == '}') {
            return emptyObjectMap();
        } else if (codePoint == -1) {
            throw new IOException("unexpected EOF");
        }

        Map<String, Object> object = newObjectMap();

        // read the first value (we've confirmed it exists)
        reader.unread(codePoint);
        readKeyValuePair(object);

        loop:
        while ((codePoint = reader.read()) != -1) {
            switch (codePoint) {
                case ',':
                    readKeyValuePair(object);
                    break;
                case '}':
                    break loop;
                default:
                    throw new IOException("unexpected '" + (char) codePoint + "' expected , or ]");
            }
        }

        if (codePoint == -1) {
            throw new IOException("unexpected EOF while reading object");
        }

        return object;
    }

    private List<Object> readArray() throws IOException {
        int codePoint = nextSignificantCodepoint();
        if (codePoint == ']') {
            return emptyArrayList();
        } else if (codePoint == -1) {
            throw new IOException("unexpected EOF");
        }

        List<Object> array = newArrayList();

        // read the first value (we've confirmed it exists)
        array.add(readJsonValue(codePoint));

        loop:
        while ((codePoint = reader.read()) != -1) {
            switch (codePoint) {
                case ',':
                    array.add(nextJsonValue());
                    break;
                case ']':
                    break loop;
                default:
                    throw new IOException("unexpected '" + (char) codePoint + "' expected , or ]");
            }
        }

        if (codePoint == -1) {
            throw new IOException("unexpected EOF while reading array");
        }

        return array;
    }

    private void readKeyValuePair(Map<String, Object> object) throws IOException {
        String key = (String) nextJsonValue();
        int codePoint = nextSignificantCodepoint();
        if (codePoint != ':') {
            throw new IOException("unexpected '" + (char) codePoint + "' expected :");
        }
        Object value = nextJsonValue();
        object.put(key, value);
    }

    private Object readJsonValue(int codePoint) throws IOException {
        switch (codePoint) {
            case '\"':
                // string
                return readStringLiteral();
            case '{':
                // object
                return readObject();
            case '[':
                // array
                return readArray();
            case 't':
                // true
                consume("rue");
                return Boolean.TRUE;
            case 'f':
                // false
                consume("alse");
                return Boolean.FALSE;
            case 'n':
                // null
                consume("ull");
                return nullValue();
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                // try read a number
                return readNumber(codePoint);
        }

        return null;
    }

    private void consume(String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            int codepoint = reader.read();
            if (codepoint != value.charAt(i)) {
                throw new IOException("Unexpected '" + (char) codepoint + "', expected '" + value.charAt(i) + '\'');
            }
        }
    }

    /**
     * Reads a String literal assuming that the Reader is currently positioned on the first character of the String,
     * or the closing quote of an empty String.
     */
    private Object readStringLiteral() throws IOException {
        sharedStringBuilder.setLength(0);

        boolean reading = true;
        while (reading) {
            int codepoint = reader.read();

            switch (codepoint) {
                case '\"':
                    reading = false;
                    break;
                case '\\':
                    // escape
                    sharedStringBuilder.append(readEscapeCharacter());
                    break;
                default:
                    sharedStringBuilder.appendCodePoint(codepoint);
                    break;
            }
        }

        // micro-optimisation for empty strings - let the compiler do the work for us and share empty string instances
        return parseString(sharedStringBuilder);
    }

    private Number readNumber(int codePoint) throws IOException {
        sharedStringBuilder.setLength(0);

        // FIXME: This implementation could also be much faster - it's pretty rubbish right now
        if (codePoint == '-' || codePoint == '+' || (codePoint >= '0' && codePoint <= '9')) {
            sharedStringBuilder.appendCodePoint(codePoint);
        } else {
            reader.unread(codePoint);
            throw new IOException("unexpected '" + (char) codePoint + '\'');
        }

        boolean hasExponent = false;

        while ((codePoint = reader.read()) != -1) {
            if (codePoint == '.' || (codePoint >= '0' && codePoint <= '9')) {
                sharedStringBuilder.appendCodePoint(codePoint);
            } else if (codePoint == 'e' || codePoint == 'E' && !hasExponent) {
                hasExponent = true;
                sharedStringBuilder.appendCodePoint(codePoint);

                codePoint = reader.read();
                if (codePoint == '-' || codePoint == '+' || (codePoint >= '0' && codePoint <= '9')) {
                    sharedStringBuilder.appendCodePoint(codePoint);
                } else {
                    throw new IOException("unexpected character in exponent: '" + (char) codePoint + '\'');
                }
            } else {
                break;
            }
        }

        // we *always* unread the last thing we read *if* it's not EOF
        if (codePoint != -1) {
            reader.unread(codePoint);
        }

        return parseNumber(sharedStringBuilder);
    }

    private char readEscapeCharacter() throws IOException {
        int codePoint = reader.read();
        switch (codePoint) {
            case '"':
            case '\'':
            case '/':
            case '\\':
                return (char) codePoint;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                return readUnicodeEscape();
        }

        return unexpectedEscapeCharacter(codePoint);
    }

    /**
     * Shared buffer for parsing unicode escape sequences \u0f0f
     */
    private final char[] unicodeEscapeBuffer = new char[4];

    private char readUnicodeEscape() throws IOException {
        reader.read(unicodeEscapeBuffer);
        return (char) (
                (hexCharToInt(unicodeEscapeBuffer[0]) << 12) +
                        (hexCharToInt(unicodeEscapeBuffer[1]) << 8) +
                        (hexCharToInt(unicodeEscapeBuffer[2]) << 4) +
                        hexCharToInt(unicodeEscapeBuffer[3])
        );
    }

    private int hexCharToInt(char ch) throws IOException {
        if (ch >= '0' && ch <= '9') return ch - 0x30;
        if (ch >= 'a' && ch <= 'f') return ch - 0x57;
        if (ch >= 'A' && ch <= 'F') return ch - 0x37;
        throw new IOException("expected a hex digit, got '" + ch + '\'');
    }

    private int nextSignificantCodepoint() throws IOException {
        int value = 0;
        while ((value = reader.read()) != -1) {
            if (!Character.isWhitespace(value)) {
                return value;
            }
        }

        return -1;
    }

    protected Map<String, Object> newObjectMap() {
        return new LinkedHashMap<>();
    }

    protected Map<String, Object> emptyObjectMap() {
        return emptyMap();
    }

    protected List<Object> newArrayList() {
        return new ArrayList<>();
    }

    protected List<Object> emptyArrayList() {
        return emptyList();
    }

    protected Object nullValue() {
        return Json.NULL;
    }

    protected Number parseNumber(CharSequence buffer) {
        try {
            return Integer.valueOf(buffer.toString());
        } catch (NumberFormatException nfe) {
            return Double.valueOf(buffer.toString());
        }
    }

    /**
     * <p>
     * Last stage before returning a parsed {@code String}, whether a literal value or property key. The
     * {@code CharSequence} passed here must not be cached and may be reused. If you want to introduce any
     * String caching or additional processing, overload this method.
     * </p><p>
     * Strings are often used in JSON to store non-string values (such as Dates, Binary Strings in Base64 encoding, etc.).
     * As such, this method does not assume a {@code String} return-type and allows for other types to be returned.
     * </p>
     *
     * @param buffer the content of the string
     * @return the value of the parsed {@code String}
     */
    protected Object parseString(CharSequence buffer) {
        return buffer.length() != 0 ? buffer.toString() : "";
    }

    protected char unexpectedEscapeCharacter(int codePoint) throws IOException {
        throw new IOException("unexpected escape character: " + (char) codePoint);
    }
}
