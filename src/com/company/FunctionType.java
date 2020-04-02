package com.company;

public enum FunctionType {
    MAP("map"),
    FILTER("filter");

    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
