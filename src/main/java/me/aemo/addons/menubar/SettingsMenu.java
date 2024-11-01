package me.aemo.addons.menubar;

import me.aemo.addons.enums.Languages;
import me.aemo.addons.enums.Themes;
import me.aemo.addons.interfaces.FontListener;
import me.aemo.addons.interfaces.LanguagesListener;
import me.aemo.addons.interfaces.ThemesListener;
import me.aemo.addons.utils.Constants;

import javax.swing.*;

public class SettingsMenu extends JMenu {

    private final LanguagesListener languageListener;
    private final ThemesListener themesListener;
    private final FontListener fontListener;

    public SettingsMenu(
            LanguagesListener languageListener,
            ThemesListener themesListener,
            FontListener fontListener) {
        this.languageListener = languageListener;
        this.themesListener = themesListener;
        this.fontListener = fontListener;

        setText(Constants.MENU_SETTINGS);
        createLanguageMenu();
        createThemesMenu();
        createFontChangeMenu();
    }

    private void createFontChangeMenu() {
        JMenuItem fontItem = new JMenuItem("Font Size");
        fontItem.addActionListener(e -> {
            if (fontListener != null) fontListener.onClick();
        });
        add(fontItem);
    }

    private void createLanguageMenu() {
        JMenu languageMenu = new JMenu(Constants.MENU_LANGUAGE);
        addMenuItem(languageMenu, "English", Languages.English);
        addMenuItem(languageMenu, "عربي", Languages.Arabic);
        add(languageMenu);
    }

    private void createThemesMenu() {
        JMenu themesMenu = new JMenu(Constants.MENU_THEMES);
        addMenuItem(themesMenu, "Default", Themes.Default);
        addMenuItem(themesMenu, "Light", Themes.FlatLight);
        addMenuItem(themesMenu, "Dark", Themes.FlatDark);
        addMenuItem(themesMenu, "Darcula", Themes.FlatDarcula);
        addMenuItem(themesMenu, "Intellij", Themes.FlatIntelliJ);
        add(themesMenu);
    }

    private <T> void addMenuItem(JMenu menu, String itemName, T value) {
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(e -> setValue(value));
        menu.add(menuItem);
    }

    private void setValue(Object value) {
        if (value instanceof Themes) {
            setTheme((Themes) value);
        } else if (value instanceof Languages) {
            setLanguage((Languages) value);
        }
    }

    private void setTheme(Themes theme) {
        if (themesListener != null) themesListener.onSetTheme(theme);
    }

    private void setLanguage(Languages language) {
        if (languageListener != null) languageListener.onSetLanguage(language);
    }
}
