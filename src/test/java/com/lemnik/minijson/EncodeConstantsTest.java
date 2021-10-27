package com.lemnik.minijson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EncodeConstantsTest {
    private StringWriter sw;

    @BeforeEach
    void setUp() {
        sw = new StringWriter();
    }

    @Test
    void encodeTrue() throws IOException {
        new JsonEncoder(sw).writeValue((Object) Boolean.TRUE);
        assertThat(sw.toString(), is("true"));
    }

    @Test
    void encodeFalse() throws IOException {
        new JsonEncoder(sw).writeValue((Object) Boolean.FALSE);
        assertThat(sw.toString(), is("false"));
    }

    @Test
    void encodeNull() throws IOException {
        new JsonEncoder(sw).writeValue((Object) null);
        assertThat(sw.toString(), is("null"));
    }

    @Test
    void encodeJsonNull() throws IOException {
        new JsonEncoder(sw).writeValue((Object) Json.NULL);
        assertThat(sw.toString(), is("null"));
    }
}
