package com.lemnik.minijson;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DecodeNumberLiteralTest {
    @Test
    void parseIntegers() throws IOException {
        Object result = new JsonDecoder("1234").nextJsonValue();
        assertThat(result, is(Integer.valueOf(1234)));

        result = new JsonDecoder(Integer.toString(Integer.MAX_VALUE)).nextJsonValue();
        assertThat(result, is(Integer.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    void parseDecimal() throws IOException {
        Object result = new JsonDecoder("1234.567").nextJsonValue();
        assertThat(result, is(Double.valueOf(1234.567)));
    }

    @Test
    void parseNegativeInteger() throws IOException {
        Object result = new JsonDecoder("-1234").nextJsonValue();
        assertThat(result, is(Integer.valueOf(-1234)));

        result = new JsonDecoder(Integer.toString(Integer.MIN_VALUE)).nextJsonValue();
        assertThat(result, is(Integer.valueOf(Integer.MIN_VALUE)));
    }

    @Test
    void parseNegativeDecimal() throws IOException {
        Object result = new JsonDecoder("-1234.5678").nextJsonValue();
        assertThat(result, is(Double.valueOf(-1234.5678)));
    }

    @Test
    void parseMinDouble() throws IOException {
        Object result = new JsonDecoder("4.9E-324").nextJsonValue();
        assertThat(result, is(Double.valueOf(Double.MIN_VALUE)));
    }

    @Test
    void parseMaxDouble() throws IOException {
        Object result = new JsonDecoder("1.7976931348623157E308").nextJsonValue();
        assertThat(result, is(Double.valueOf(Double.MAX_VALUE)));
    }
}
