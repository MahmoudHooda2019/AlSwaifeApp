package me.aemo.addons;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Utils {
    public static void loadIcon(JFrame frame, String iconName) {
        URL iconUrl = Utils.class.getClassLoader().getResource(iconName);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            frame.setIconImage(icon.getImage());
        }
    }


    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 30));
        return textField;
    }

    public static double parseDouble(Component component, String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            showError(component, "Please enter valid numerical values.");
            return -1;
        }
    }

    public static void showError(Component component, String message) {
        JOptionPane.showMessageDialog(component, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}
