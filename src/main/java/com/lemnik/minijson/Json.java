package com.lemnik.minijson;

import java.io.IOException;

public class Json {

    /**
     * Sentinel object to differentiate a {@code null} literal in a JSON stream from a Java {@code null}.
     */
    public static final Object NULL = new Object();

    private Json() {
    }

    public static Object parse(String json) {
        try {
            return (new JsonDecoder(json)).nextJsonValue();
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
}
