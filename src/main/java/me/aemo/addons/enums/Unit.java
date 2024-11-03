package me.aemo.addons.enums;

public enum Unit {
    METERS("سم"), // m
    CENTIMETERS("متر"); // cm

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Unit fromLabel(String label) {
        for (Unit unit : values()) {
            if (unit.getLabel().equals(label)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("No unit with label " + label);
    }
}
