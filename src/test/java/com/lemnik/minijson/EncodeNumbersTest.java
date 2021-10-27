package com.lemnik.minijson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EncodeNumbersTest {
    private StringWriter sw;

    @BeforeEach
    void setUp() {
        sw = new StringWriter();
    }

    @Test
    void encodeInteger() throws IOException {
        new JsonEncoder(sw).writeValue(Integer.valueOf(123987));
        assertThat(sw.toString(), is("123987"));
    }

    @Test
    void encodeMinInteger() throws IOException {
        new JsonEncoder(sw).writeValue(Integer.valueOf(Integer.MIN_VALUE));
        assertThat(sw.toString(), is(Integer.toString(Integer.MIN_VALUE)));
    }

    @Test
    void encodeMaxInteger() throws IOException {
        new JsonEncoder(sw).writeValue(Integer.valueOf(Integer.MAX_VALUE));
        assertThat(sw.toString(), is(Integer.toString(Integer.MAX_VALUE)));
    }

    @Test
    void encodeDouble() throws IOException {
        new JsonEncoder(sw).writeValue(Double.valueOf(10.5f));
        assertThat(sw.toString(), is("10.5"));
    }

    @Test
    void testMinDouble() throws IOException {
        new JsonEncoder(sw).writeValue(Double.valueOf(Double.MIN_VALUE));
        assertThat(sw.toString(), is("4.9E-324"));
    }

    @Test
    void testMaxDouble() throws IOException {
        new JsonEncoder(sw).writeValue(Double.valueOf(Double.MAX_VALUE));
        assertThat(sw.toString(), is("1.7976931348623157E308"));
    }
}
