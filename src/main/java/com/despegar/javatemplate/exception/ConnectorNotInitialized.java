package com.despegar.javatemplate.exception;

public class ConnectorNotInitialized extends RuntimeException {

    public ConnectorNotInitialized(String restConnectorName, Throwable throwable) {
        super("The " + restConnectorName + " rest connect couldn't be initialized", throwable);
    }
}
