package me.aemo;


import com.formdev.flatlaf.FlatLightLaf;
import me.aemo.addons.data.Item;
import me.aemo.addons.data.ProductEntry;
import me.aemo.addons.menubar.ToolBar;
import me.aemo.addons.product.ProductsUtils;
import me.aemo.addons.utils.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.aemo.addons.utils.Utils.showError;

public class UI extends JFrame {
    private JComboBox<String> itemComboBox;
    private JTextField quantityField, lengthField, heightField, surfaceField, priceField;
    private JButton saveButton, addButton, displayButton;

    private JComboBox<String> lengthUnitComboBox, heightUnitComboBox;
    private Map<String, Item> itemMap;
    private final ProductsUtils productsUtils;
    private final List<ProductEntry> productEntries;
    private DefaultTableModel tableModel;

    public UI() {
        setTitle("AlSwaife");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        ToolBar toolBar = new ToolBar(
                this,
                language -> JOptionPane.showMessageDialog(null, "Language set to " + language.name() + "."),
                theme -> {
                    try {
                        Utils.changeUITheme(UI.this, theme);
                    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                             IllegalAccessException e) {
                        showError(this, "Change Theme UI Error: " + e.getMessage());
                    }
                },
                website -> Utils.openHelpWebsite(UI.this, website),
                () -> Utils.showFontSizeDialog(UI.this)
        );
        toolBar.setBackground(Color.GRAY);
        contentPane.add(toolBar, BorderLayout.NORTH);

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
        // Initialize the table model
        String[] columnNames = {"الصنف", "العدد", "الطول", "الارتفاع", "المسطح", "سعر المتر", "الاجمالي"};
        tableModel = new DefaultTableModel(columnNames, 0);

        initializeFields();
        initializeComboBox();
        initializeButtons();
        addComponents(gbc, mainPanel);
        setupListeners();

        itemComboBox.setSelectedIndex(0);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        Utils.loadIcon(this, "logo.jpg");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeFields() {
        quantityField = Utils.createTextField();
        lengthField = Utils.createTextField();
        heightField = Utils.createTextField();
        surfaceField = Utils.createTextField();
        surfaceField.setEditable(false);
        priceField = Utils.createTextField();
        priceField.setEditable(false);

        String[] units = {"متر", "سم"};
        lengthUnitComboBox = new JComboBox<>(units);
        heightUnitComboBox = new JComboBox<>(units);
    }

    private void initializeComboBox() {
        itemMap = new HashMap<>();
        Item[] items = productsUtils.getAllItems();
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
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveToExcel());

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addProduct());

        displayButton = new JButton("Display All");
        displayButton.addActionListener(e -> displayAllEntries());
    }

    private void addComponents(GridBagConstraints gbc, JPanel panel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("الصنف:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("العدد:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("الطول:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(lengthField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(lengthUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("الارتفاع:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(heightField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(heightUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("المسطح:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(surfaceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("سعر المتر:"), gbc);

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
            double length = parseLength(lengthField.getText(), (String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem()));
            double height = parseHeight(heightField.getText(), (String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem()));
            double surface = quantity * length * height;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            surfaceField.setText(decimalFormat.format(surface));
        } catch (NumberFormatException e) {
            surfaceField.setText("");
        }
    }

    private double parseLength(String lengthText, String unit) {
        double length = Double.parseDouble(lengthText);
        return unit.equals("سم") ? length / 100 : length; // Convert cm to m
    }

    private double parseHeight(String heightText, String unit) {
        double height = Double.parseDouble(heightText);
        return unit.equals("سم") ? height / 100 : height; // Convert cm to m
    }

    private void addProduct() {
        try {
            String selectedName = (String) itemComboBox.getSelectedItem();
            double quantity = Double.parseDouble(quantityField.getText());
            double length = parseLength(lengthField.getText(), (String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem()));
            double height = parseHeight(heightField.getText(), (String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem()));
            double surface = quantity * length * height;
            double price = Double.parseDouble(priceField.getText());
            double total = quantity * price;

            productEntries.add(new ProductEntry(selectedName, quantity, length, height, surface, price, total));
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            updateTableModel();
            clearFields();
        } catch (NumberFormatException e) {
            showError(this, "Please enter valid numerical values.");
        }
    }

    private void displayAllEntries() {
        JDialog dialog = new JDialog(this, "All Products", false); // Non-modal dialog
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
        String[] columnNames = {"الصنف", "العدد", "الطول", "الارتفاع", "المسطح", "سعر المتر", "الاجمالي"};
        Object[][] data = new Object[productEntries.size()][columnNames.length];

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

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        return new JTable(tableModel);
    }

    private JPanel createButtonPanel(JTable table) {
        JButton editButton = new JButton("Edit");
        JButton removeSelectedButton = new JButton("Remove");
        JButton removeAllButton = new JButton("Remove All");
        JButton okButton = new JButton("OK");

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
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
        }
    }

    private void showEditDialog(ProductEntry entry) {
        JDialog editDialog = new JDialog(this, "Edit Product", true);
        JPanel editPanel = new JPanel(new GridLayout(0, 2));

        JComboBox<String> editItemComboBox = new JComboBox<>(itemMap.keySet().toArray(new String[0]));
        editItemComboBox.setSelectedItem(entry.getItemName());

        JTextField editQuantityField = new JTextField(String.valueOf(entry.getQuantity()));
        JTextField editLengthField = new JTextField(String.valueOf(entry.getLength()));
        JComboBox<String> editLengthUnitComboBox = new JComboBox<>(new String[]{"متر", "سم"});
        editLengthUnitComboBox.setSelectedItem(entry.getLength() < 1 ? "سم" : "متر");

        JTextField editHeightField = new JTextField(String.valueOf(entry.getHeight()));
        JComboBox<String> editHeightUnitComboBox = new JComboBox<>(new String[]{"متر", "سم"});
        editHeightUnitComboBox.setSelectedItem(entry.getHeight() < 1 ? "سم" : "متر");

        JTextField editSurfaceField = new JTextField(String.valueOf(entry.getSurface()));
        editSurfaceField.setEditable(false);

        JTextField editPriceField = new JTextField(String.valueOf(entry.getPrice()));
        editPriceField.setEditable(false);

        // Add components to edit panel
        editPanel.add(new JLabel("الصنف:"));
        editPanel.add(editItemComboBox);
        editPanel.add(new JLabel("العدد:"));
        editPanel.add(editQuantityField);
        editPanel.add(new JLabel("الطول:"));
        editPanel.add(editLengthField);
        editPanel.add(new JLabel("الوحدة:"));
        editPanel.add(editLengthUnitComboBox);
        editPanel.add(new JLabel("الارتفاع:"));
        editPanel.add(editHeightField);
        editPanel.add(new JLabel("الوحدة:"));
        editPanel.add(editHeightUnitComboBox);
        editPanel.add(new JLabel("المسطح:"));
        editPanel.add(editSurfaceField);
        editPanel.add(new JLabel("سعر المتر:"));
        editPanel.add(editPriceField);

        // Update price field when item changes
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
        JButton saveEditButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveEditButton.addActionListener(e -> {
            try {
                // Validate and update entry
                double quantity = Double.parseDouble(editQuantityField.getText());
                double length = parseLength(editLengthField.getText(), (String) Objects.requireNonNull(editLengthUnitComboBox.getSelectedItem()));
                double height = parseHeight(editHeightField.getText(), (String) Objects.requireNonNull(editHeightUnitComboBox.getSelectedItem()));
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
                showError(this, "Please enter valid numerical values.");
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
            double length = parseLength(lengthField.getText(), (String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem()));
            double height = parseHeight(heightField.getText(), (String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem()));
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
            JOptionPane.showMessageDialog(this, "Please select a product to remove.");
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

        // Use try-with-resources to ensure the workbook is closed automatically
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            createHeaderRow(sheet);

            for (int i = 0; i < productEntries.size(); i++) {
                ProductEntry entry = productEntries.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(entry.getItemName());
                row.createCell(1).setCellValue(entry.getQuantity());
                row.createCell(2).setCellValue(entry.getLength());
                row.createCell(3).setCellValue(entry.getHeight());
                row.createCell(4).setCellValue(entry.getSurface());
                row.createCell(5).setCellValue(entry.getPrice());
                row.createCell(6).setCellValue(entry.getTotal());
            }

            // Write the workbook to the output file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(this, "Data saved to " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                showError(this, "Error saving data to Excel: " + e.getMessage());
            }
        } catch (IOException e) {
            showError(this, "Error creating workbook: " + e.getMessage());
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"الصنف", "العدد", "الطول", "الارتفاع", "المسطح", "سعر المتر", "الاجمالي"};
        for (int i = 0; i < columnHeaders.length; i++) {
            headerRow.createCell(i).setCellValue(columnHeaders[i]);
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
