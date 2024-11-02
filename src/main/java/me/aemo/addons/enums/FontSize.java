package me.aemo.addons.enums;

public enum FontSize {
    SMALL(12),
    MEDIUM(16),
    LARGE(20);

    private final int size;

    FontSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
