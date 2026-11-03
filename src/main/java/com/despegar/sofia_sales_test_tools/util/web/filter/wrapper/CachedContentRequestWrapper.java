package com.despegar.sofia_sales_test_tools.util.web.filter.wrapper;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class CachedContentRequestWrapper extends HttpServletRequestWrapper {

    public CachedContentRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private byte[] bodyAsBytes;

    private byte[] getBodyAsBytes() {
        if (bodyAsBytes == null) {
            try {
                bodyAsBytes = IOUtils.toByteArray(super.getInputStream());
            } catch (Exception e) {
                // do nothing
            }

        }
        return bodyAsBytes;
    }

    public ServletInputStream getInputStream() {
        return new Wrapper(new ByteArrayInputStream(getBodyAsBytes()));
    }

    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
