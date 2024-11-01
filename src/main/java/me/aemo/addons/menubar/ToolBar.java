package me.aemo.addons.menubar;

import me.aemo.addons.interfaces.FontListener;
import me.aemo.addons.interfaces.HelpListener;
import me.aemo.addons.interfaces.LanguagesListener;
import me.aemo.addons.interfaces.ThemesListener;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JToolBar {

    public ToolBar(JFrame frame,
                   LanguagesListener languagesListener,
                   ThemesListener themesListener,
                   HelpListener helpListener,
                   FontListener fontListener
    ) {
        JMenuBar menuBar = createMenuBar(languagesListener, themesListener, helpListener, fontListener);
        frame.setJMenuBar(menuBar);
        frame.add(this, BorderLayout.NORTH);
    }

    private JMenuBar createMenuBar(
            LanguagesListener languagesListener,
            ThemesListener themesListener,
            HelpListener helpListener,
            FontListener fontListener) {
        JMenuBar menuBar = new JMenuBar();

        // Add settings menu
        SettingsMenu settingsMenu = new SettingsMenu(languagesListener, themesListener, fontListener);
        menuBar.add(settingsMenu);

        // Add help menu
        HelpMenu helpMenu = new HelpMenu();
        helpMenu.setListener(helpListener);
        menuBar.add(helpMenu);

        return menuBar;
    }
}
