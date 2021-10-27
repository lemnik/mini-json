package com.lemnik.minijson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EncodeArrayTest {
    private StringWriter sw;

    @BeforeEach
    void setUp() {
        sw = new StringWriter();
    }

    @Test
    void encodeMixedConstantArray() throws IOException {
        new JsonEncoder(sw).writeValue(Arrays.asList(
                null,
                Boolean.TRUE,
                Json.NULL,
                Boolean.FALSE
        ));

        assertThat(sw.toString(), is("[null,true,null,false]"));
    }

    @Test
    void encodeIntegerArray() throws IOException {
        new JsonEncoder(sw).writeValue(Arrays.asList(
                Integer.valueOf(1),
                Integer.valueOf(2),
                Integer.valueOf(3),
                Integer.valueOf(4),
                Integer.valueOf(5)
        ));

        assertThat(sw.toString(), is("[1,2,3,4,5]"));
    }
}
