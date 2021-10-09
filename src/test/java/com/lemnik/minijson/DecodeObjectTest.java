package com.lemnik.minijson;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeObjectTest {
    @Test
    public void simpleObject() throws IOException {
        Object result = new JsonDecoder("{\"yes\": true, \"no\": false}").nextJsonValue();

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("yes", Boolean.TRUE);
        expected.put("no", Boolean.FALSE);

        assertThat(result, is(expected));
    }

    @Test
    public void emptyObject() throws IOException {
        Object result = new JsonDecoder("{}").nextJsonValue();
        assertThat(result, is(emptyMap()));
    }

    @Test
    public void nestedObject() throws IOException {
        Object result = new JsonDecoder("{\"array\": [1, 2, 3], \"object\": {\"yes\": true}}").nextJsonValue();

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("array", Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)));

        Map<String, Object> nestedObject = new LinkedHashMap<>();
        nestedObject.put("yes", Boolean.TRUE);
        expected.put("object", nestedObject);

        assertThat(result, is(expected));
    }

    @Test
    public void incompleteObject() {
        assertThrows(IOException.class, () -> new JsonDecoder("{\"yes\": true").nextJsonValue());
    }

    @Test
    public void malformedObject() {
        assertThrows(IOException.class, () -> new JsonDecoder("{\"yes\"; true").nextJsonValue());
    }
}
