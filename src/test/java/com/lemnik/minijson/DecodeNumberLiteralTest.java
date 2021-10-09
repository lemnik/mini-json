package com.lemnik.minijson;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DecodeNumberLiteralTest {
    @Test
    public void parseIntegers() throws IOException {
        Object result = new JsonDecoder("1234").nextJsonValue();
        assertThat(result, is(Integer.valueOf(1234)));

        result = new JsonDecoder(Integer.toString(Integer.MAX_VALUE)).nextJsonValue();
        assertThat(result, is(Integer.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void parseDecimal() throws IOException {
        Object result = new JsonDecoder("1234.567").nextJsonValue();
        assertThat(result, is(Double.valueOf(1234.567)));
    }

    @Test
    public void parseNegativeInteger() throws IOException {
        Object result = new JsonDecoder("-1234").nextJsonValue();
        assertThat(result, is(Integer.valueOf(-1234)));

        result = new JsonDecoder(Integer.toString(Integer.MIN_VALUE)).nextJsonValue();
        assertThat(result, is(Integer.valueOf(Integer.MIN_VALUE)));
    }

    @Test
    public void parseNegativeDecimal() throws IOException {
        Object result = new JsonDecoder("-1234.5678").nextJsonValue();
        assertThat(result, is(Double.valueOf(-1234.5678)));
    }
}
