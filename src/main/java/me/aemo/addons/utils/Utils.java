package me.aemo.addons.utils;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import me.aemo.addons.enums.HelpWebsite;
import me.aemo.addons.enums.Themes;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
    private final JFrame jframe;

    public Utils(JFrame jframe){
        this.jframe = jframe;
    }

    public void updateUIFont(Font newFont) {
        for (Component component : jframe.getContentPane().getComponents()) {
            setFontRecursively(component, newFont);
        }
    }

    private void setFontRecursively(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setFontRecursively(child, font);
            }
        }
    }

    public void open(File file) throws IOException {
        if (file.isDirectory()){
            openFolder(file);
        } else {
            openFile(file);
        }
    }
    private void openFolder(File file) throws IOException {
        Desktop.getDesktop().open(file.getParentFile());
    }
    private void openFile(File file) throws IOException {
        Desktop.getDesktop().open(file);

    }


    public void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showDialog(String msg){
        JOptionPane.showMessageDialog(jframe, msg);
    }
    public void changeUITheme(Themes theme) {
        switch (theme) {
            case Default -> changeLook(UIManager.getCrossPlatformLookAndFeelClassName());
            case FlatLight -> changeLook(new FlatLightLaf());
            case FlatDark -> changeLook(new FlatDarkLaf());
            case FlatDarcula -> changeLook(new FlatDarculaLaf());
            case FlatIntelliJ -> changeLook(new FlatIntelliJLaf());
        }

        updateUI();
    }


    public void changeLook(Object look) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (look instanceof String) {
                    UIManager.setLookAndFeel((String) look);
                } else if (look instanceof LookAndFeel) {
                    UIManager.setLookAndFeel((LookAndFeel) look);
                } else {
                    showError("نوع المظهر غير المدعوم: " + look.getClass().getName());
                }
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                     InstantiationException | IllegalAccessException e) {
                showError("فشل في تعيين المظهر والشكل، خطأ: " + e.getMessage());
            }
        });
    }

    public void updateUI(){
        SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(jframe));
    }


    public void openHelpWebsite(HelpWebsite website) {
        switch (website) {
            case GITHUB -> openLink(Constants.GITHUB_URL);
            case FACEBOOK -> openLink(Constants.FACEBOOK_URL);
        }
    }

    public void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            showError("فشل في فتح الرابط: " + e.getMessage());
        }
    }

    public void loadIcon(JFrame frame, String iconName) {
        URL iconUrl = Utils.class.getClassLoader().getResource(iconName);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            frame.setIconImage(icon.getImage());
        }
    }


    public JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 35));
        return textField;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(jframe, message, "رسالة خطأ", JOptionPane.ERROR_MESSAGE);
    }

}
