package me.aemo.addons.menubar;

import me.aemo.addons.interfaces.*;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JToolBar {

    public ToolBar(JFrame frame,
                   FileChooseListener fileChooseListener,
                   ThemesListener themesListener,
                   HelpListener helpListener,
                   FontSizeListener fontSizeListener,
                   FontStyleListener fontStyleListener
    ) {
        JMenuBar menuBar = createMenuBar(fileChooseListener, themesListener, helpListener, fontSizeListener, fontStyleListener);
        frame.setJMenuBar(menuBar);
        frame.add(this, BorderLayout.NORTH);
    }

    private JMenuBar createMenuBar(
            FileChooseListener fileChooseListener,
            ThemesListener themesListener,
            HelpListener helpListener,
            FontSizeListener fontSizeListener,
            FontStyleListener fontStyleListener) {
        JMenuBar menuBar = new JMenuBar();

        // Add File Menu
        FileMenu fileMenu = new FileMenu();
        fileMenu.setListener(fileChooseListener);
        menuBar.add(fileMenu);

        // Add settings menu
        SettingsMenu settingsMenu = new SettingsMenu(themesListener, fontSizeListener, fontStyleListener);
        menuBar.add(settingsMenu);

        // Add help menu
        HelpMenu helpMenu = new HelpMenu();
        helpMenu.setListener(helpListener);
        menuBar.add(helpMenu);

        return menuBar;
    }
}
