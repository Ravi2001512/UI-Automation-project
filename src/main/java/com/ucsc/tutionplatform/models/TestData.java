package com.ucsc.tutionplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestData<T> {
    private String name;
    private T data;

    // Default Constructor
    public TestData() {}

    // Parameterized Constructor
    public TestData(String name, T data) {
        this.name = name;
        this.data = data;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
