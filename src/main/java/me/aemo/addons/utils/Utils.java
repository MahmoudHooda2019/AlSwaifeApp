package me.aemo.addons.utils;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import me.aemo.addons.enums.HelpWebsite;
import me.aemo.addons.enums.Themes;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Utils {
    public static void showFontSizeDialog(Component component) {
        JDialog dialog = new JDialog((Frame) component, "Enter Font Size and Style", true);
        dialog.setSize(350, 150);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(component); // Center the dialog relative to the parent frame

        // Create a main panel with BoxLayout to stack components vertically
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center main panel

        // Font size panel
        JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center content
        JLabel sizeLabel = new JLabel("Enter font size (numbers only):");
        JTextField numberField = new JTextField(10);
        numberField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                if (str.matches("\\d*")) { // Allow only digits
                    super.insertString(offset, str, a);
                }
            }
        });
        fontSizePanel.add(sizeLabel);
        fontSizePanel.add(numberField);

        // Font style panel
        JPanel fontStylePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center content
        JLabel styleLabel = new JLabel("Select font style:");
        String[] styles = {"Plain", "Bold", "Italic"};
        JComboBox<String> styleSpinner = new JComboBox<>(styles);
        fontStylePanel.add(styleLabel);
        fontStylePanel.add(styleSpinner);

        // Add the font panels to the main panel
        mainPanel.add(fontSizePanel);
        mainPanel.add(fontStylePanel);

        // Add a small space between panels
        mainPanel.add(Box.createVerticalStrut(10)); // Adds vertical spacing

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center content
        JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            String text = numberField.getText();
            if (!text.isEmpty()) {
                int fontSize = Integer.parseInt(text);
                int fontStyle = switch (Objects.requireNonNull(styleSpinner.getSelectedItem()).toString()) {
                    case "Bold" -> Font.BOLD;
                    case "Italic" -> Font.ITALIC;
                    default -> Font.PLAIN;
                };
                Utils.UIFont(fontStyle, fontSize); // Change font size and style
                Utils.updateUI(component); // Update the UI
                dialog.dispose(); // Close the dialog
            }
        });
        buttonPanel.add(okButton);

        // Add the button panel to the main panel
        mainPanel.add(buttonPanel);

        // Add the main panel to the dialog
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public static void changeUITheme(Component component, Themes theme) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        switch (theme){
            case Default -> Utils.changeLook(UIManager.getCrossPlatformLookAndFeelClassName());
            case FlatLight -> Utils.changeLook(new FlatLightLaf());
            case FlatDark -> Utils.changeLook(new FlatDarkLaf());
            case FlatDarcula -> Utils.changeLook(new FlatDarculaLaf());
            case FlatIntelliJ -> Utils.changeLook(new FlatIntelliJLaf());
        }
        Utils.updateUI(component);
    }

    public static void openHelpWebsite(Component component, HelpWebsite website) {
        switch (website) {
            case GITHUB -> Utils.openLink(component, Constants.GITHUB_URL);
            case FACEBOOK -> Utils.openLink(component, Constants.FACEBOOK_URL);
        }
    }


    public static void changeLook(Object look) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (look instanceof String){
            UIManager.setLookAndFeel((String) look);
        } else if (look instanceof LookAndFeel){
            UIManager.setLookAndFeel((LookAndFeel) look);
        }
    }
    public static void updateUI(Component component){
        SwingUtilities.updateComponentTreeUI(component);
    }

    public static void UIFont(int style, int size) { // 12

        Font font = new Font("Arial", style, size);

        UIManager.put("TextField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
    }

    public static void openLink(Component component, String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            showError(component, "Failed to open link: " + e.getMessage());
        }
    }

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

    /*
    public static double parseDouble(Component component, String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            showError(component, "Please enter valid numerical values.");
            return -1;
        }
    }
    */

    public static void showError(Component component, String message) {
        JOptionPane.showMessageDialog(component, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}
