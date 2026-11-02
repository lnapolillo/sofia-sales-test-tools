package com.despegar.sofia_sales_test_tools.config.compression.httpfilters.request;

import com.despegar.sofia_sales_test_tools.config.compression.CompressionMethodConfig;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompressionRequestWrapperTest {
    @Mock
    private HttpServletRequest request;
    @Mock private CompressionMethodConfig compressionMethodConfig;
    @Mock private ServletInputStream servletInputStream;

    private CompressionRequestWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new CompressionRequestWrapper(request, compressionMethodConfig);
    }

    @Test
    void testGetInputStreamReturnsStream() {
        when(compressionMethodConfig.createServletInputStream(request)).thenReturn(servletInputStream);
        ServletInputStream result = wrapper.getInputStream();
        assertEquals(servletInputStream, result);
        assertEquals(servletInputStream, wrapper.getInputStream());
    }

    @Test
    void testGetInputStreamThrowsIfReaderCalled() {
        when(compressionMethodConfig.createServletInputStream(request)).thenReturn(servletInputStream);
        wrapper.getReader();
        assertThrows(IllegalStateException.class, () -> wrapper.getInputStream());
    }

    @Test
    void testGetReaderReturnsBufferedReader() {
        when(compressionMethodConfig.createServletInputStream(request)).thenReturn(servletInputStream);
        BufferedReader reader = wrapper.getReader();
        assertNotNull(reader);
        assertEquals(reader, wrapper.getReader());
    }

    @Test
    void testGetReaderThrowsIfInputStreamCalled() {
        when(compressionMethodConfig.createServletInputStream(request)).thenReturn(servletInputStream);
        wrapper.getInputStream();
        assertThrows(IllegalStateException.class, () -> wrapper.getReader());
    }

    @Test
    void testFinishRequestClosesReader() throws IOException {
        BufferedReader reader =
            spy(
                new BufferedReader(
                    new InputStreamReader(mock(InputStream.class), StandardCharsets.UTF_8)));
        CompressionRequestWrapper testWrapper =
            new CompressionRequestTestWrapper(request, compressionMethodConfig, reader, null);
        testWrapper.finishRequest();
        verify(reader).close();
    }

    @Test
    void testFinishRequestClosesStream() throws IOException {
        ServletInputStream stream = mock(ServletInputStream.class);
        CompressionRequestWrapper testWrapper =
            new CompressionRequestTestWrapper(request, compressionMethodConfig, null, stream);
        testWrapper.finishRequest();
        verify(stream).close();
    }

    @Test
    void testFinishRequestIgnoresIOException() throws IOException {
        BufferedReader reader = mock(BufferedReader.class);
        doThrow(new IOException("fail")).when(reader).close();
        CompressionRequestWrapper testWrapper =
            new CompressionRequestTestWrapper(request, compressionMethodConfig, reader, null);
        assertDoesNotThrow(testWrapper::finishRequest);
    }

    @Test
    void testFinishRequestNothingToClose() {
        CompressionRequestWrapper testWrapper =
            new CompressionRequestTestWrapper(request, compressionMethodConfig, null, null);
        assertDoesNotThrow(testWrapper::finishRequest);
    }

    private static class CompressionRequestTestWrapper extends CompressionRequestWrapper {
        public CompressionRequestTestWrapper(
            HttpServletRequest request,
            CompressionMethodConfig compressionMethodConfig,
            BufferedReader reader,
            ServletInputStream stream) {
            super(request, compressionMethodConfig);
            this.reader = reader;
            this.stream = stream;
        }

        @Override
        public BufferedReader getReader() {
            return Objects.requireNonNullElse(reader, super.getReader());
        }

        @Override
        public ServletInputStream getInputStream() {
            return Objects.requireNonNullElse(stream, super.getInputStream());
        }
    }
}
