package com.despegar.sofia_sales_test_tools.util.web.filter.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class Wrapper extends ServletInputStream {

    private final ByteArrayInputStream is;

    public Wrapper(ByteArrayInputStream is) {
        this.is = is;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() {
        return is.read();
    }
}
