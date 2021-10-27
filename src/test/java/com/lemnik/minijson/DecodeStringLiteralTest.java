package com.lemnik.minijson;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeStringLiteralTest {
    @Test
    void simpleStringLiteral() throws IOException {
        Object result = new JsonDecoder("\"String literal here\"").nextJsonValue();
        assertThat(result, is("String literal here"));
    }

    @Test
    void escapeSequences() throws IOException {
        /*
         escape
            '"'
            '\'
            '/'
            'b'
            'f'
            'n'
            'r'
            't'
         */
        Object result = new JsonDecoder("\"\\\" \\\\ \\/ \\b \\f \\n \\r \\t\"").nextJsonValue();
        assertThat(result, is("\" \\ / \b \f \n \r \t"));
    }

    @Test
    void unicodeStringLiteral() throws IOException {
        Object result = new JsonDecoder("\"Привет 你好 안녕하십니까 今日は！\"").nextJsonValue();
        assertThat(result, is("Привет 你好 안녕하십니까 今日は！"));
    }

    @Test
    void unicodeEscape_Uppercase() throws IOException {
        Object result = new JsonDecoder("" +
                "\"\\u041F\\u0440\\u0438\\u0432\\u0435\\u0442\\u0020\\u4F60\\u597D\\u0020" +
                "\\uC548\\uB155\\uD558\\uC2ED\\uB2C8\\uAE4C\\u0020\\u4ECA\\u65E5\\u306F\\uFF01\""
        ).nextJsonValue();
        assertThat(result, is("Привет 你好 안녕하십니까 今日は！"));
    }

    @Test
    void unicodeEscape_Lowercase() throws IOException {
        Object result = new JsonDecoder("" +
                "\"\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442\\u0020\\u4f60\\u597d\\u0020" +
                "\\uc548\\ub155\\ud558\\uc2ed\\ub2c8\\uae4c\\u0020\\u4eca\\u65e5\\u306f\\uff01\""
        ).nextJsonValue();
        assertThat(result, is("Привет 你好 안녕하십니까 今日は！"));
    }

    @Test
    void badEscapeCharacter() {
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\k\"").nextJsonValue());
    }

    @Test
    void badUnicodeEscapes() {
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\uZFDC\"").nextJsonValue());
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\u;[]1\"").nextJsonValue());
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\u0f\"").nextJsonValue());
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\u[]\"").nextJsonValue());
        assertThrows(IOException.class, () -> new JsonDecoder("\"\\u\"").nextJsonValue());
    }
}