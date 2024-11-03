package me.aemo.addons.menubar;

import me.aemo.addons.enums.FontSize;
import me.aemo.addons.enums.FontStyle;
import me.aemo.addons.enums.Themes;
import me.aemo.addons.interfaces.FontSizeListener;
import me.aemo.addons.interfaces.FontStyleListener;
import me.aemo.addons.interfaces.ThemesListener;
import me.aemo.addons.utils.Constants;

import javax.swing.*;

public class SettingsMenu extends JMenu {
    private final ThemesListener themesListener;
    private final FontSizeListener fontSizeListener;
    private final FontStyleListener fontStyleListener;
    private final ButtonGroup languageGroup = new ButtonGroup();
    private final ButtonGroup themeGroup = new ButtonGroup();
    private final ButtonGroup fontSizeGroup = new ButtonGroup();
    private final ButtonGroup fontStyleGroup = new ButtonGroup();

    public SettingsMenu(
            ThemesListener themesListener,
            FontSizeListener fontSizeListener,
            FontStyleListener fontStyleListener) {
        this.themesListener = themesListener;
        this.fontSizeListener = fontSizeListener;
        this.fontStyleListener = fontStyleListener;

        setText(Constants.MENU_SETTINGS);
        createThemesMenu();
        createFontSizeMenu();
        createFontStyleMenu();
    }
    private void createFontStyleMenu(){
        JMenu fontItem = new JMenu(Constants.MENU_FONT_STYLE);
        JRadioButtonMenuItem item = addRadioButtonMenuItem(fontItem, "سهل", FontStyle.PLAIN);
        addRadioButtonMenuItem(fontItem, "عريض", FontStyle.BOLD);
        addRadioButtonMenuItem(fontItem, "مائل", FontStyle.ITALIC);
        addRadioButtonMenuItem(fontItem, "غامق مائل", FontStyle.BOLD_ITALIC);
        item.setSelected(true);
        add(fontItem);
    }


    private void createFontSizeMenu() {
        JMenu fontItem = new JMenu(Constants.MENU_FONT_SIZE);
        addRadioButtonMenuItem(fontItem, "صغير", FontSize.SMALL);
        JRadioButtonMenuItem mediumBtnItem = addRadioButtonMenuItem(fontItem, "متوسط", FontSize.MEDIUM);
        addRadioButtonMenuItem(fontItem, "كبير", FontSize.LARGE);
        mediumBtnItem.setSelected(true);
        add(fontItem);
    }

    private void createThemesMenu() {
        JMenu themesMenu = new JMenu(Constants.MENU_THEMES);
        JRadioButtonMenuItem lightItem = addRadioButtonMenuItem(themesMenu, "ضوء", Themes.FlatLight);
        addRadioButtonMenuItem(themesMenu, "الإفتراضي", Themes.Default);
        addRadioButtonMenuItem(themesMenu, "مظلم", Themes.FlatDark);
        addRadioButtonMenuItem(themesMenu, "داركولا", Themes.FlatDarcula);
        addRadioButtonMenuItem(themesMenu, "انتلييج", Themes.FlatIntelliJ);
        lightItem.setSelected(true);
        add(themesMenu);
    }

    private <T> JRadioButtonMenuItem addRadioButtonMenuItem(JMenu menu, String itemName, T value) {
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(itemName);
        menuItem.addActionListener(e -> setValue(value));
        menu.add(menuItem);
        if (value instanceof Themes) {
            themeGroup.add(menuItem);
        } else if (value instanceof FontSize){
            fontSizeGroup.add(menuItem);
        } else if (value instanceof FontStyle) {
            fontStyleGroup.add(menuItem);
        }
        return menuItem;
    }

    private void setValue(Object value) {
        if (value instanceof Themes) {
            setTheme((Themes) value);
        } else if (value instanceof FontSize){
            setFontSize((FontSize) value);
        } else if (value instanceof FontStyle) {
            setFontStyle((FontStyle) value);
        }
    }

    private void setFontStyle(FontStyle fontStyle) {
        if (fontStyleListener != null) fontStyleListener.onChoose(fontStyle);
    }

    private void setFontSize(FontSize fontSize){
        if (fontSizeListener != null) fontSizeListener.onChoose(fontSize);
    }
    private void setTheme(Themes theme) {
        if (themesListener != null) themesListener.onSetTheme(theme);
    }
}
