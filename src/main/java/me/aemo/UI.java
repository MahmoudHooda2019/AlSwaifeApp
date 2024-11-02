package me.aemo;


import com.formdev.flatlaf.FlatLightLaf;
import me.aemo.addons.data.Item;
import me.aemo.addons.data.ProductEntry;
import me.aemo.addons.enums.FontSize;
import me.aemo.addons.enums.FontStyle;
import me.aemo.addons.enums.Unit;
import me.aemo.addons.menubar.ToolBar;
import me.aemo.addons.product.ProductsUtils;
import me.aemo.addons.utils.Constants;
import me.aemo.addons.utils.Translator;
import me.aemo.addons.utils.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UI extends JFrame {
    private JComboBox<String> itemComboBox;
    private JTextField quantityField, lengthField, heightField, surfaceField, priceField;
    private JButton saveButton, addButton, displayButton;

    private JComboBox<String> lengthUnitComboBox, heightUnitComboBox;
    private Map<String, Item> itemMap;
    private final ProductsUtils productsUtils;
    private final List<ProductEntry> productEntries;
    private DefaultTableModel tableModel;

    private final Translator translator;
    private final Utils utils;
    private final String[] unitsList;
    private final Map<JComponent, String> componentMap = new HashMap<>();

    private void updateUIComponents() {
        for (Map.Entry<JComponent, String> entry : componentMap.entrySet()) {
            updateComponentText(entry.getKey(), entry.getValue());
        }
    }

    private void updateComponentText(JComponent component, String translationKey) {
        if (component instanceof JButton) {
            ((JButton) component).setText(translator.translate(translationKey));
        } else if (component instanceof JLabel) {
            ((JLabel) component).setText(translator.translate(translationKey));
        }

        /*
        else if (component instanceof JComboBox) {
            JComboBox<String> comboBox = (JComboBox<String>) component;
            String[] items = new String[comboBox.getItemCount()];

            for (int i = 0; i < comboBox.getItemCount(); i++) {
                items[i] = comboBox.getItemAt(i);
            }

            // Clear the combo box
            comboBox.removeAllItems();

            // Add translated items
            for (String item : items) {
                comboBox.addItem(translator.translate(item));
            }
        }
        */
    }


    public UI() {
        translator = new Translator();
        utils = new Utils(this);

        setTitle("AlSwaife");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        ToolBar toolBar = new ToolBar(
                this,
                language -> {
                    translator.setLanguage(language);
                    updateUIComponents();
                }
                ,
                utils::changeUITheme,
                utils::openHelpWebsite,
                this::onFontSizeChange,
                this::onFontStyleChange
        );
        toolBar.setBackground(Color.GRAY);
        contentPane.add(toolBar, BorderLayout.NORTH);

        Unit[] units = Unit.values();
        unitsList = new String[units.length];
        for (int i = 0; i < units.length; i++){
            unitsList[i] = translator.translate(units[i].getLabel());
        }


        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        productEntries = new ArrayList<>();
        try {
            productsUtils = new ProductsUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tableModel = new DefaultTableModel(Constants.COLUMN_NAMES, 0);

        initializeFields();
        initializeComboBox();
        initializeButtons();
        addComponents(gbc, mainPanel);
        setupListeners();


        itemComboBox.setSelectedIndex(0);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        utils.loadIcon(this, Constants.ICON);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void onFontSizeChange(FontSize fontSize) {
        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), fontSize.getSize());
        updateUIFont(newFont);
    }

    private void onFontStyleChange(FontStyle fontStyle) {
        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getName(), fontStyle.getStyle(), currentFont.getSize());
        updateUIFont(newFont);
    }

    // update the font of UI components
    private void updateUIFont(Font newFont) {
        for (Component component : getContentPane().getComponents()) {
            setFontRecursively(component, newFont);
        }
    }

    // set the font for components
    private void setFontRecursively(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setFontRecursively(child, font);
            }
        }
    }


    private void initializeFields() {
        quantityField = utils.createTextField();
        lengthField = utils.createTextField();
        heightField = utils.createTextField();
        surfaceField = utils.createTextField();
        surfaceField.setEditable(false);
        priceField = utils.createTextField();
        priceField.setEditable(false);


        assert unitsList != null;
        lengthUnitComboBox = new JComboBox<>(unitsList);
        heightUnitComboBox = new JComboBox<>(unitsList);
    }

    private void initializeComboBox() {
        itemMap = new HashMap<>();
        Item[] items = productsUtils.getAllItems(); // Make sure this method doesn't return null

        if (items == null || items.length == 0) {
            // Handle the case where there are no items
            System.out.println("No items available.");
            itemComboBox = new JComboBox<>(); // Initialize with an empty combo box
            return; // Exit early
        }

        String[] itemNames = new String[items.length];

        for (int i = 0; i < items.length; i++) {
            itemNames[i] = items[i].getName();
            itemMap.put(itemNames[i], items[i]);
        }

        itemComboBox = new JComboBox<>(itemNames);
        itemComboBox.addActionListener(e -> {
            String selectedName = (String) itemComboBox.getSelectedItem();
            Item selectedItem = itemMap.get(selectedName);
            priceField.setText(selectedItem != null ? String.valueOf(selectedItem.getPrice()) : "0");
        });
    }


    private void initializeButtons() {
        saveButton = new JButton(translator.translate("save"));
        componentMap.put(saveButton, "save");
        saveButton.addActionListener(e -> saveToExcel());

        addButton = new JButton(translator.translate("add"));
        componentMap.put(addButton, "add");
        addButton.addActionListener(e -> addProduct());

        displayButton = new JButton(translator.translate("display_all"));
        componentMap.put(displayButton, "display_all");
        displayButton.addActionListener(e -> displayAllEntries());
    }

    private void addComponents(GridBagConstraints gbc, JPanel panel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel productLabel = new JLabel(translator.translate("product") + ":");
        componentMap.put(productLabel, "product");
        panel.add(productLabel, gbc);


        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel quantityLabel = new JLabel(translator.translate("quantity") + ":");
        componentMap.put(quantityLabel, "quantity");
        panel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;

        JLabel lengthLabel = new JLabel(translator.translate("length") + ":");
        componentMap.put(lengthLabel, "length");
        panel.add(lengthLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(lengthField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(lengthUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;

        JLabel heightLabel = new JLabel(translator.translate("height") + ":");
        componentMap.put(heightLabel, "height");
        panel.add(heightLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(heightField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(heightUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel surfaceLabel = new JLabel(translator.translate("surface") + ":");
        componentMap.put(surfaceLabel, "surface");
        panel.add(surfaceLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(surfaceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel priceLabel = new JLabel(translator.translate("price") + ":");
        componentMap.put(priceLabel, "price");
        panel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(priceField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(displayButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
    }

    private void setupListeners() {
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSurfaceField();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSurfaceField();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSurfaceField();
            }
        };

        quantityField.getDocument().addDocumentListener(listener);
        lengthField.getDocument().addDocumentListener(listener);
        heightField.getDocument().addDocumentListener(listener);
    }

    private void updateSurfaceField() {
        try {
            double quantity = Double.parseDouble(quantityField.getText());
            double length = parseLength(lengthField.getText(), Unit.valueOf((String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem())));
            double height = parseHeight(heightField.getText(), Unit.valueOf((String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem())));
            double surface = quantity * length * height;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            surfaceField.setText(decimalFormat.format(surface));
        } catch (NumberFormatException e) {
            surfaceField.setText("");
        }
    }

    private double parseLength(String lengthText, Unit unit) {
        double length = Double.parseDouble(lengthText);
        return unit.getLabel().equals(translator.translate("cm_unit")) ? length / 100 : length; // Convert cm to m
    }

    private double parseHeight(String heightText, Unit unit) {
        double height = Double.parseDouble(heightText);
        return unit.getLabel().equals(translator.translate("cm_unit")) ? height / 100 : height; // Convert cm to m
    }

    private void addProduct() {
        try {
            String selectedName = (String) itemComboBox.getSelectedItem();
            double quantity = Double.parseDouble(quantityField.getText());
            double length = parseLength(lengthField.getText(), Unit.valueOf((String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem())));
            double height = parseHeight(heightField.getText(), Unit.valueOf((String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem())));
            double surface = quantity * length * height;
            double price = Double.parseDouble(priceField.getText());
            double total = quantity * price;

            productEntries.add(new ProductEntry(selectedName, quantity, length, height, surface, price, total));
            utils.showDialog(translator.translate("product_added"));
            updateTableModel();
            clearFields();
        } catch (NumberFormatException e) {
            utils.showError(translator.translate("valid_num"));
        }
    }

    private void displayAllEntries() {
        JDialog dialog = new JDialog(this, translator.translate("all_product"), false); // Non-modal dialog
        JTable table = createProductTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = createButtonPanel(table);

        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTable createProductTable() {
        Object[][] data = new Object[productEntries.size()][Constants.COLUMN_NAMES.length];

        for (int i = 0; i < productEntries.size(); i++) {
            ProductEntry entry = productEntries.get(i);
            data[i] = new Object[]{
                    entry.getItemName(),
                    entry.getQuantity(),
                    entry.getLength(),
                    entry.getHeight(),
                    entry.getSurface(),
                    entry.getPrice(),
                    entry.getTotal()
            };
        }

        tableModel = new DefaultTableModel(data, Constants.COLUMN_NAMES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        return new JTable(tableModel);
    }

    private JPanel createButtonPanel(JTable table) {
        JButton editButton = new JButton(translator.translate("edit"));
        JButton removeSelectedButton = new JButton(translator.translate("remove"));
        JButton removeAllButton = new JButton(translator.translate("remove_all"));
        JButton okButton = new JButton(translator.translate("ok"));

        editButton.addActionListener(e -> editSelectedProduct(table));
        removeSelectedButton.addActionListener(e -> removeSelectedProduct(table));
        removeAllButton.addActionListener(e -> removeAllProducts());
        okButton.addActionListener(e -> ((JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, okButton)).dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(removeAllButton);
        buttonPanel.add(okButton);
        return buttonPanel;
    }

    private void editSelectedProduct(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            ProductEntry selectedEntry = productEntries.get(selectedRow);
            showEditDialog(selectedEntry);
        } else {
            utils.showDialog(translator.translate("select_product"));
        }
    }

    private void showEditDialog(ProductEntry entry) {
        JDialog editDialog = new JDialog(this, translator.translate("edit_product"), true);
        JPanel editPanel = new JPanel(new GridLayout(0, 2));

        JComboBox<String> editItemComboBox = new JComboBox<>(itemMap.keySet().toArray(new String[0]));
        editItemComboBox.setSelectedItem(entry.getItemName());

        JTextField editQuantityField = new JTextField(String.valueOf(entry.getQuantity()));
        JTextField editLengthField = new JTextField(String.valueOf(entry.getLength()));

        String cm = translator.translate("cm_unit");
        //String m = translator.translate("m_unit");

        JComboBox<String> editLengthUnitComboBox = new JComboBox<>(unitsList);
        editLengthUnitComboBox.setSelectedItem(entry.getLength() < 1 ? cm : 1);

        JTextField editHeightField = new JTextField(String.valueOf(entry.getHeight()));
        JComboBox<String> editHeightUnitComboBox = new JComboBox<>(unitsList);
        editHeightUnitComboBox.setSelectedItem(entry.getHeight() < 1 ? cm : 1);

        JTextField editSurfaceField = new JTextField(String.valueOf(entry.getSurface()));
        editSurfaceField.setEditable(false);

        JTextField editPriceField = new JTextField(String.valueOf(entry.getPrice()));
        editPriceField.setEditable(false);

        // Add components to edit panel
        editPanel.add(new JLabel(translator.translate("product") + ":"));
        editPanel.add(editItemComboBox);
        editPanel.add(new JLabel(translator.translate("quantity") +":"));
        editPanel.add(editQuantityField);
        editPanel.add(new JLabel(translator.translate("length") + ":"));
        editPanel.add(editLengthField);
        editPanel.add(new JLabel(translator.translate("unit") + ":"));
        editPanel.add(editLengthUnitComboBox);
        editPanel.add(new JLabel(translator.translate("height") + ":"));
        editPanel.add(editHeightField);
        editPanel.add(new JLabel(translator.translate("unit") + ":"));
        editPanel.add(editHeightUnitComboBox);
        editPanel.add(new JLabel(translator.translate("surface") + ":"));
        editPanel.add(editSurfaceField);
        editPanel.add(new JLabel(translator.translate("price") + ":"));
        editPanel.add(editPriceField);

        editItemComboBox.addActionListener(e -> {
            String selectedItem = (String) editItemComboBox.getSelectedItem();
            Item item = itemMap.get(selectedItem);
            if (item != null) {
                editPriceField.setText(String.valueOf(item.getPrice()));
            }
        });

        // Document listener to update surface field
        DocumentListener surfaceListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSurfaceField(editQuantityField, editLengthField, editLengthUnitComboBox, editHeightField, editHeightUnitComboBox, editSurfaceField);
            }

            public void removeUpdate(DocumentEvent e) {
                updateSurfaceField(editQuantityField, editLengthField, editLengthUnitComboBox, editHeightField, editHeightUnitComboBox, editSurfaceField);
            }

            public void changedUpdate(DocumentEvent e) {
                updateSurfaceField(editQuantityField, editLengthField, editLengthUnitComboBox, editHeightField, editHeightUnitComboBox, editSurfaceField);
            }
        };

        editQuantityField.getDocument().addDocumentListener(surfaceListener);
        editLengthField.getDocument().addDocumentListener(surfaceListener);
        editHeightField.getDocument().addDocumentListener(surfaceListener);

        // Create button panel for save/cancel actions
        JButton saveEditButton = new JButton(translator.translate("save"));
        JButton cancelButton = new JButton(translator.translate("cancel"));

        saveEditButton.addActionListener(e -> {
            try {
                // Validate and update entry
                double quantity = Double.parseDouble(editQuantityField.getText());
                double length = parseLength(editLengthField.getText(), Unit.valueOf((String) Objects.requireNonNull(editLengthUnitComboBox.getSelectedItem())));
                double height = parseHeight(editHeightField.getText(), Unit.valueOf((String) Objects.requireNonNull(editHeightUnitComboBox.getSelectedItem())));
                double surface = quantity * length * height;
                double price = Double.parseDouble(editPriceField.getText());

                // Update entry
                entry.setQuantity(quantity);
                entry.setLength(length);
                entry.setHeight(height);
                entry.setSurface(surface);
                entry.setTotal(quantity * price);

                // Update table and close dialog
                updateTableModel();
                editDialog.dispose();
            } catch (NumberFormatException ex) {
                utils.showError(translator.translate("valid_num"));
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        // Add button panel to dialog
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveEditButton);
        buttonPanel.add(cancelButton);

        editDialog.setLayout(new BorderLayout());
        editDialog.add(editPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void updateSurfaceField(JTextField quantityField, JTextField lengthField, JComboBox<String> lengthUnitComboBox,
                                    JTextField heightField, JComboBox<String> heightUnitComboBox, JTextField surfaceField) {
        try {
            double quantity = Double.parseDouble(quantityField.getText());
            double length = parseLength(lengthField.getText(), Unit.valueOf((String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem())));
            double height = parseHeight(heightField.getText(), Unit.valueOf((String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem())));
            double surface = quantity * length * height;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            surfaceField.setText(decimalFormat.format(surface));
        } catch (NumberFormatException e) {
            surfaceField.setText("");
        }
    }


    private void removeSelectedProduct(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            productEntries.remove(selectedRow);
            updateTableModel();
        } else {
            utils.showDialog("Please select a product to remove.");
        }
    }

    private void removeAllProducts() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove all products?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            productEntries.clear();
            updateTableModel();
        }
    }

    private void clearFields() {
        quantityField.setText("");
        lengthField.setText("");
        heightField.setText("");
        surfaceField.setText("");
        priceField.setText("");
        itemComboBox.setSelectedIndex(0);
        setupListeners();
    }

    private void saveToExcel() {
        // Determine the path to the Documents folder
        String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
        File outputFile = new File(documentsPath, "products.xlsx");


        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            createHeaderRow(sheet);

            for (int i = 0; i < productEntries.size(); i++) {
                ProductEntry entry = productEntries.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(Constants.ITEM_COLUMN).setCellValue(entry.getItemName());
                row.createCell(Constants.QUANTITY_COLUMN).setCellValue(entry.getQuantity());
                row.createCell(Constants.LENGTH_COLUMN).setCellValue(entry.getLength());
                row.createCell(Constants.HEIGHT_COLUMN).setCellValue(entry.getHeight());
                row.createCell(Constants.SURFACE_COLUMN).setCellValue(entry.getSurface());
                row.createCell(Constants.PRICE_COLUMN).setCellValue(entry.getPrice());
                row.createCell(Constants.TOTAL_COLUMN).setCellValue(entry.getTotal());
            }


            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
                showSaveDialog(outputFile);
            } catch (IOException e) {
                utils.showError("Error saving data to Excel: " + e.getMessage());
            }
        } catch (IOException e) {
            utils.showError("Error creating workbook: " + e.getMessage());
        }
    }

    private void showSaveDialog(File outputFile) throws IOException {
        Object[] options = {
                translator.translate("open_file"),
                translator.translate("open_location"),
                translator.translate("close")};

        int choice = JOptionPane.showOptionDialog(
                null,
                translator.translate("saved_to") + outputFile.getAbsolutePath(),
                translator.translate("file_saved"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        switch (choice) {
            case 0: // Open File
                utils.open(outputFile);
                break;
            case 1: // Open Location
                utils.open(outputFile);
                break;
            case 2: // Close
                break;
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < Constants.COLUMN_NAMES.length; i++) {
            headerRow.createCell(i).setCellValue(Constants.COLUMN_NAMES[i]);
        }
    }

    private void updateTableModel() {
        tableModel.setRowCount(0);
        for (ProductEntry entry : productEntries) {
            tableModel.addRow(new Object[]{
                    entry.getItemName(),
                    entry.getQuantity(),
                    entry.getLength(),
                    entry.getHeight(),
                    entry.getSurface(),
                    entry.getPrice(),
                    entry.getTotal()
            });
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatLightLaf());
        SwingUtilities.invokeLater(UI::new);
    }
}