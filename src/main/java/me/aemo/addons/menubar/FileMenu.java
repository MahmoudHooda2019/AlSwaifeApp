package me.aemo.addons.menubar;


import me.aemo.addons.interfaces.FileChooseListener;
import me.aemo.addons.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FileMenu extends JMenu {

    private FileChooseListener listener;

    public FileMenu() {
        setText(Constants.MENU_FILE);
        createFileItems();
    }

    public void setListener(FileChooseListener listener) {
        this.listener = listener;
    }

    private void createFileItems() {
        addMenuItem(Constants.MENU_ITEM_IMPORT, e -> {
            if (listener != null) listener.onImportClick();
        });
        addMenuItem(Constants.MENU_ITEM_EXPORT, e -> {
            if (listener != null) listener.onExportClick();
        });
    }

    private void addMenuItem(String itemName, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(actionListener);
        add(menuItem);
    }
}
