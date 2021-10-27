package com.lemnik.minijson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeConstantsTest {
    @Test
    void parseTrue() throws IOException {
        Object result = new JsonDecoder("true").nextJsonValue();
        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    void parseFalse() throws IOException {
        Object result = new JsonDecoder("false").nextJsonValue();
        assertThat(result, is(Boolean.FALSE));
    }

    @Test
    void parseNull() throws IOException {
        Object result = new JsonDecoder("null").nextJsonValue();
        assertThat(result, is(Json.NULL));
    }

    @Test
    void parseFalse_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("fault").nextJsonValue());
    }

    @Test
    void parseTrue_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("truple").nextJsonValue());
    }

    @Test
    void parseNull_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("nulk").nextJsonValue());
    }
}
