package me.aemo.addons.menubar;

import me.aemo.addons.enums.FontSize;
import me.aemo.addons.enums.FontStyle;
import me.aemo.addons.enums.Languages;
import me.aemo.addons.enums.Themes;
import me.aemo.addons.interfaces.FontSizeListener;
import me.aemo.addons.interfaces.FontStyleListener;
import me.aemo.addons.interfaces.LanguagesListener;
import me.aemo.addons.interfaces.ThemesListener;
import me.aemo.addons.utils.Constants;

import javax.swing.*;

public class SettingsMenu extends JMenu {

    private final LanguagesListener languageListener;
    private final ThemesListener themesListener;
    private final FontSizeListener fontSizeListener;
    private final FontStyleListener fontStyleListener;
    private final ButtonGroup languageGroup = new ButtonGroup();
    private final ButtonGroup themeGroup = new ButtonGroup();
    private final ButtonGroup fontSizeGroup = new ButtonGroup();
    private final ButtonGroup fontStyleGroup = new ButtonGroup();

    public SettingsMenu(
            LanguagesListener languageListener,
            ThemesListener themesListener,
            FontSizeListener fontSizeListener,
            FontStyleListener fontStyleListener) {

        this.languageListener = languageListener;
        this.themesListener = themesListener;
        this.fontSizeListener = fontSizeListener;
        this.fontStyleListener = fontStyleListener;

        setText(Constants.MENU_SETTINGS);
        createLanguageMenu();
        createThemesMenu();
        createFontSizeMenu();
        createFontStyleMenu();
    }
    private void createFontStyleMenu(){
        JMenu fontItem = new JMenu(Constants.MENU_FONT_SIZE);
        JRadioButtonMenuItem item = addRadioButtonMenuItem(fontItem, "Plain", FontStyle.PLAIN);
        addRadioButtonMenuItem(fontItem, "Bold", FontStyle.BOLD);
        addRadioButtonMenuItem(fontItem, "Italic", FontStyle.ITALIC);
        item.setSelected(true);
        add(fontItem);
    }


    private void createFontSizeMenu() {
        JMenu fontItem = new JMenu(Constants.MENU_FONT_SIZE);
        addRadioButtonMenuItem(fontItem, "Small", FontSize.SMALL);
        JRadioButtonMenuItem mediumBtnItem = addRadioButtonMenuItem(fontItem, "Medium", FontSize.MEDIUM);
        addRadioButtonMenuItem(fontItem, "Big", FontSize.LARGE);
        mediumBtnItem.setSelected(true);
        add(fontItem);
    }

    private void createLanguageMenu() {
        JMenu languageMenu = new JMenu(Constants.MENU_LANGUAGE);
        JRadioButtonMenuItem englishItem = addRadioButtonMenuItem(languageMenu, "English", Languages.English);
        addRadioButtonMenuItem(languageMenu, "عربي", Languages.Arabic);
        englishItem.setSelected(true);
        add(languageMenu);
    }

    private void createThemesMenu() {
        JMenu themesMenu = new JMenu(Constants.MENU_THEMES);
        JRadioButtonMenuItem lightItem = addRadioButtonMenuItem(themesMenu, "Light", Themes.FlatLight);
        addRadioButtonMenuItem(themesMenu, "Default", Themes.Default);
        addRadioButtonMenuItem(themesMenu, "Dark", Themes.FlatDark);
        addRadioButtonMenuItem(themesMenu, "Darcula", Themes.FlatDarcula);
        addRadioButtonMenuItem(themesMenu, "Intellij", Themes.FlatIntelliJ);
        lightItem.setSelected(true);
        add(themesMenu);
    }

    private <T> JRadioButtonMenuItem addRadioButtonMenuItem(JMenu menu, String itemName, T value) {
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(itemName);
        menuItem.addActionListener(e -> setValue(value));
        menu.add(menuItem);
        if (value instanceof Languages) {
            languageGroup.add(menuItem);
        } else if (value instanceof Themes) {
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
        } else if (value instanceof Languages) {
            setLanguage((Languages) value);
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

    private void setLanguage(Languages language) {
        if (languageListener != null) languageListener.onSetLanguage(language);
    }
}
