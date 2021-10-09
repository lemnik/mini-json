package com.lemnik.minijson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeConstantsTest {
    @Test
    public void parseTrue() throws IOException {
        Object result = new JsonDecoder("true").nextJsonValue();
        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void parseFalse() throws IOException {
        Object result = new JsonDecoder("false").nextJsonValue();
        assertThat(result, is(Boolean.FALSE));
    }

    @Test
    public void parseNull() throws IOException {
        Object result = new JsonDecoder("null").nextJsonValue();
        assertThat(result, is(Json.NULL));
    }

    @Test
    public void parseFalse_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("fault").nextJsonValue());
    }

    @Test
    public void parseTrue_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("truple").nextJsonValue());
    }

    @Test
    public void parseNull_Negative() throws IOException {
        assertThrows(IOException.class, () -> new JsonDecoder("nulk").nextJsonValue());
    }
}
