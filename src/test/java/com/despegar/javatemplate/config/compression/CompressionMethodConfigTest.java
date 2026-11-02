package com.despegar.javatemplate.config.compression;

import static com.despegar.javatemplate.config.compression.CompressionMethodConfig.GZIP;
import static com.despegar.javatemplate.config.compression.CompressionMethodConfig.SNAPPY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CompressionMethodConfigTest {

    @Test
    void nullHeaderShouldReturnNullTest() {
        assertNull(CompressionMethodConfig.resolve(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "not_compression_header"})
    void emptyOrNotCompressionHeaderShouldReturnNullTest(String header) {
        assertNull(CompressionMethodConfig.resolve(header));
    }

    @Test
    void snappyCompressionHeaderTest() {
        assertEquals(SNAPPY, CompressionMethodConfig.resolve("x-snappy"));
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "gzip, not_compression_header",
            "some-header, gzip , not_compression_header",
            "some-header,another-header, gzip   "
        })
    void gzipHeaderShouldReturnGZIPTest(String header) {
        assertEquals(GZIP, CompressionMethodConfig.resolve(header));
    }
}
