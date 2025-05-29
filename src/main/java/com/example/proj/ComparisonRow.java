package com.example.proj;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ComparisonRow {
    private final StringProperty param;
    private final StringProperty customValue;
    private final StringProperty tspValue;

    public ComparisonRow(String param, String customValue, String tspValue) {
        this.param = new SimpleStringProperty(param);
        this.customValue = new SimpleStringProperty(customValue);
        this.tspValue = new SimpleStringProperty(tspValue);
    }

    public StringProperty paramProperty() { return param; }
    public StringProperty customValueProperty() { return customValue; }
    public StringProperty tspValueProperty() { return tspValue; }
}

