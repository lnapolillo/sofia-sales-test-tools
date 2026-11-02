package com.despegar.javatemplate.model.metric;

public enum BusinessLabel {

    SOME_LABEL("someLabel");

    private final String name;

    BusinessLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
