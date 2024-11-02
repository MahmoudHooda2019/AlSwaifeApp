package me.aemo.addons.menubar;

import me.aemo.addons.interfaces.*;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JToolBar {

    public ToolBar(JFrame frame,
                   LanguagesListener languagesListener,
                   ThemesListener themesListener,
                   HelpListener helpListener,
                   FontSizeListener fontSizeListener,
                   FontStyleListener fontStyleListener
    ) {
        JMenuBar menuBar = createMenuBar(languagesListener, themesListener, helpListener, fontSizeListener, fontStyleListener);
        frame.setJMenuBar(menuBar);
        frame.add(this, BorderLayout.NORTH);
    }

    private JMenuBar createMenuBar(
            LanguagesListener languagesListener,
            ThemesListener themesListener,
            HelpListener helpListener,
            FontSizeListener fontSizeListener,
            FontStyleListener fontStyleListener) {
        JMenuBar menuBar = new JMenuBar();

        // Add settings menu
        SettingsMenu settingsMenu = new SettingsMenu(languagesListener, themesListener, fontSizeListener, fontStyleListener);
        menuBar.add(settingsMenu);

        // Add help menu
        HelpMenu helpMenu = new HelpMenu();
        helpMenu.setListener(helpListener);
        menuBar.add(helpMenu);

        return menuBar;
    }
}
