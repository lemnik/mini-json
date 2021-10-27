package com.lemnik.minijson;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JsonEncoder {
    protected final Writer writer;

    public JsonEncoder(Writer writer) {
        this.writer = writer;
    }

    public final void writeValue(Map<String, ? extends Object> object) throws IOException {
        if (object == null) {
            writeNull();
            return;
        }

        writer.write('{');

        // avoid entrySet / Iterator allocation if the object is empty
        if (!object.isEmpty()) {
            // stupid checked-exceptions mean we can't map.forEach without wrapping / unwrapping exceptions
            Iterator<? extends Map.Entry<String, ?>> iterator = object.entrySet().iterator();
            Map.Entry<String, ? extends Object> entry = iterator.next(); // we already checked for empty

            // write the first entry
            writeValue(entry.getKey());
            writer.write(':');
            writeValue(entry.getValue());

            while (iterator.hasNext()) {
                entry = iterator.next();

                writer.write(',');
                writeValue(entry.getKey());
                writer.write(':');
                writeValue(entry.getValue());
            }
        }

        writer.write('}');
    }

    public final void writeValue(Collection<? extends Object> array) throws IOException {
        if (array == null) {
            writeNull();
            return;
        }

        writer.write('[');

        // avoid Iterator allocation if the array is empty
        if (!array.isEmpty()) {
            Iterator<? extends Object> iterator = array.iterator();

            // write the first value, confirmed by previous isEmpty check
            Object value = iterator.next();
            writeValue(value);

            while (iterator.hasNext()) {
                value = iterator.next();

                writer.write(',');
                writeValue(value);
            }
        }

        writer.write(']');
    }

    public final void writeValue(CharSequence string) throws IOException {
        if (string == null) {
            writeNull();
            return;
        }

        int length = string.length();

        writer.write('"');
        for (int i = 0; i < length; i++) {
            char ch = string.charAt(i);
            switch (ch) {
                case '\"':
                    writer.write('\\');
                    writer.write('"');
                    break;
                case '\\':
                    writer.write('\\');
                    writer.write('\\');
                    break;
                case '\b':
                    writer.write('\\');
                    writer.write('b');
                    break;
                case '\f':
                    writer.write('\\');
                    writer.write('f');
                    break;
                case '\n':
                    writer.write('\\');
                    writer.write('n');
                    break;
                case '\r':
                    writer.write('\\');
                    writer.write('r');
                    break;
                case '\t':
                    writer.write('\\');
                    writer.write('t');
                    break;
                default:
                    writer.write(ch);
                    break;
            }
        }
        writer.write('"');
    }

    public final void writeValue(Number value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }

        writer.write(value.toString());
    }

    public final void writeValue(int value) throws IOException {
        writer.write(Integer.toString(value));
    }

    public final void writeValue(double value) throws IOException {
        writer.write(Double.toString(value));
    }

    public final void writeValue(Boolean value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }

        writer.write(value.toString());
    }

    public final void writeValue(boolean value) throws IOException {
        writeValue(Boolean.toString(value));
    }

    public final void writeNull() throws IOException {
        writer.write("null");
    }

    @SuppressWarnings("unchecked")
    protected void writeValue(Object object) throws IOException {
        if (object instanceof Map) writeValue((Map<String, ? extends Object>) object);
        else if (object instanceof CharSequence) writeValue((CharSequence) object);
        else if (object instanceof Collection) writeValue((Collection<? extends Object>) object);
        else if (object instanceof Number) writeValue((Number) object);
        else if (object instanceof Boolean) writeValue((Boolean) object);
        else if (object == null || object == Json.NULL) writeNull();
        else throw new IllegalArgumentException("cannot stringify unknown type: " + object);
    }
}
