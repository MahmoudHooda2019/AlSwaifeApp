package me.aemo;

import me.aemo.addons.Language;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ToolBar extends JToolBar {

    private final JFrame frame;

    public ToolBar(JFrame frame) {
        this.frame = frame;
        initializeToolBar();
    }

    private void initializeToolBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu settingsMenu = new JMenu("Settings");

        JMenu languageMenu = new JMenu("Language");
        JMenuItem englishItem = new JMenuItem("English");
        englishItem.addActionListener(e -> setLanguage(Language.English));
        JMenuItem arabicItem = new JMenuItem("عربي");
        arabicItem.addActionListener(e -> setLanguage(Language.Arabic));
        languageMenu.add(englishItem);
        languageMenu.add(arabicItem);
        settingsMenu.add(languageMenu);

        // Preferences menu item (placeholder)
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> showSettings());
        settingsMenu.add(preferencesItem);
        menuBar.add(settingsMenu);

        // Create "Help" menu
        JMenu helpMenu = new JMenu("Help");

        // GitHub menu item
        JMenuItem helpItem = new JMenuItem("Our GitHub");
        helpItem.addActionListener(e -> openGitHub());
        helpMenu.add(helpItem);

        // Facebook menu item
        JMenuItem facebookItem = new JMenuItem("Our Facebook");
        facebookItem.addActionListener(e -> openFacebook());
        helpMenu.add(facebookItem);

        menuBar.add(helpMenu);

        // Add the menu bar to the frame
        frame.setJMenuBar(menuBar);

        // Add the toolbar to the top of the frame
        frame.add(this, BorderLayout.NORTH);
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(frame, "Settings Dialog (to be implemented)");
    }

    private void setLanguage(Language language) {
        JOptionPane.showMessageDialog(frame, "Language set to " + language.name() + ".");
    }

    private void openGitHub() {
        openLink("https://github.com/MahmoudHooda2019/");
    }

    private void openFacebook() {
        openLink("https://www.facebook.com/profile.php?id=100012640485216");
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(frame, "Failed to open link: " + e.getMessage());
        }
    }
}
