package me.aemo.addons.enums;

import java.awt.Font;

public enum FontStyle {
    PLAIN(Font.PLAIN),
    BOLD(Font.BOLD),
    ITALIC(Font.ITALIC),
    BOLD_ITALIC(Font.BOLD | Font.ITALIC);

    private final int style;

    FontStyle(int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }
}
