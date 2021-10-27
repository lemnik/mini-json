package com.lemnik.minijson;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Json {

    /**
     * Sentinel object to differentiate a {@code null} literal in a JSON stream from a Java {@code null}.
     */
    public static final Object NULL = new Object();

    private static Function<Writer, JsonEncoder> ENCODER_FACTORY = JsonEncoder::new;

    private static Function<Reader, JsonDecoder> READER_DECODER_FACTORY = JsonDecoder::new;
    private static Function<String, JsonDecoder> STRING_DECODER_FACTORY = JsonDecoder::new;

    private Json() {
    }

    public static void setEncoderFactory(Function<Writer, JsonEncoder> encoderFactory) {
        ENCODER_FACTORY = encoderFactory;
    }

    public static void setReaderDecoderFactory(Function<Reader, JsonDecoder> readerDecoderFactory) {
        READER_DECODER_FACTORY = readerDecoderFactory;
    }

    public static void setStringDecoderFactory(Function<String, JsonDecoder> stringDecoderFactory) {
        STRING_DECODER_FACTORY = stringDecoderFactory;
    }

    public static Object parse(String json) {
        try {
            return STRING_DECODER_FACTORY.apply(json).nextJsonValue();
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
}
