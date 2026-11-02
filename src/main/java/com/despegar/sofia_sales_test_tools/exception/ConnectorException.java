package com.despegar.sofia_sales_test_tools.exception;

public class ConnectorException extends RuntimeException {

    public ConnectorException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public ConnectorException(String msg) {
        super(msg);
    }
}
