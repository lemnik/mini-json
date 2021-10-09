package com.lemnik.minijson;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeArrayTest {
    @Test
    public void arrayWithLiterals() throws IOException {
        Object result = new JsonDecoder("[321, false, true, null, \"test string\", 123.45]").nextJsonValue();
        assertThat(
                (Iterable<Object>) result,
                contains(
                        Integer.valueOf(321),
                        Boolean.FALSE,
                        Boolean.TRUE,
                        Json.NULL,
                        "test string",
                        Double.valueOf(123.45)
                )
        );
    }

    @Test
    public void arrayOfArrays() throws IOException {
        Object result = new JsonDecoder("[321, [123, 456], []]").nextJsonValue();
        assertThat(
                (Iterable<Object>) result,
                contains(
                        Integer.valueOf(321),
                        Arrays.asList(
                                Integer.valueOf(123),
                                Integer.valueOf(456)
                        ),
                        emptyList()
                )
        );
    }

    @Test
    public void emptyArray() throws IOException {
        Object result = new JsonDecoder("[]").nextJsonValue();
        assertThat(result, is(emptyList()));
    }

    @Test
    public void unclosedArray() {
        assertThrows(IOException.class, () -> new JsonDecoder("[123, 432").nextJsonValue());
    }

    @Test
    public void emptyArrayElements() {
        assertThrows(IOException.class, () -> new JsonDecoder("[,,123,]").nextJsonValue());
    }
}
