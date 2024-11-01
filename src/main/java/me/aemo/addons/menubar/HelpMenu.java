package me.aemo.addons.menubar;

import me.aemo.addons.enums.HelpWebsite;
import me.aemo.addons.interfaces.HelpListener;
import me.aemo.addons.utils.Constants;

import javax.swing.*;

public class HelpMenu extends JMenu {

    private HelpListener listener;

    public HelpMenu() {
        setText(Constants.MENU_HELP);
        createHelpItems();
    }

    public void setListener(HelpListener listener) {
        this.listener = listener;
    }

    private void createHelpItems() {
        addMenuItem(Constants.MENU_ITEM_GITHUB, HelpWebsite.GITHUB);
        addMenuItem(Constants.MENU_ITEM_FACEBOOK, HelpWebsite.FACEBOOK);
    }

    private void addMenuItem(String itemName, HelpWebsite website) {
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(e -> handleWebsiteClick(website));
        add(menuItem);
    }

    private void handleWebsiteClick(HelpWebsite website) {
        if (listener != null) {
            listener.onClick(website);
        }
    }
}
