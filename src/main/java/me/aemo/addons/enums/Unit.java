package me.aemo.addons.enums;

public enum Unit {
    METERS("m"), // m
    CENTIMETERS("cm"); //cm

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
