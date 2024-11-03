package me.aemo;


import com.formdev.flatlaf.FlatLightLaf;
import me.aemo.addons.data.Item;
import me.aemo.addons.data.ProductEntry;
import me.aemo.addons.enums.FontSize;
import me.aemo.addons.enums.FontStyle;
import me.aemo.addons.enums.Unit;
import me.aemo.addons.interfaces.FileChooseListener;
import me.aemo.addons.menubar.ToolBar;
import me.aemo.addons.product.ProductsUtils;
import me.aemo.addons.ui.MyButton;
import me.aemo.addons.utils.Constants;
import me.aemo.addons.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static me.aemo.addons.utils.Constants.SPACING;

public class UI extends JFrame {
    private JComboBox<String> itemComboBox;
    private JTextField quantityField, lengthField, heightField, surfaceField, priceField;
    private MyButton saveButton, addButton, displayButton;

    private JComboBox<String> lengthUnitComboBox, heightUnitComboBox;
    private Map<String, Item> itemMap;
    private final ProductsUtils productsUtils;
    private final List<ProductEntry> productEntries;
    private DefaultTableModel tableModel;

    private final Utils utils;
    private final String[] unitsList;


    public UI() {
        utils = new Utils(this);

        setTitle("AlSwaife - السويفي");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        ToolBar toolBar = new ToolBar(
                this,
                new FileChooseListener() {
                    @Override
                    public void onImportClick() {
                        File file = chooseExcelFile();
                        if (file != null){
                            importFromExcel(file);
                        }
                    }

                    @Override
                    public void onExportClick() {
                        saveToExcel();
                    }
                },
                utils::changeUITheme,
                utils::openHelpWebsite,
                this::onFontSizeChange,
                this::onFontStyleChange
        );
        toolBar.setBackground(Color.LIGHT_GRAY);
        contentPane.add(toolBar, BorderLayout.NORTH);

        Unit[] units = Unit.values();
        unitsList = new String[units.length];
        for (int i = 0; i < units.length; i++){
            unitsList[i] = units[i].getLabel();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(SPACING, SPACING, SPACING, SPACING);

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

        setupKeyBindings(mainPanel);
        utils.loadIcon(this, Constants.ICON);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        // Bind Plus to increase size
        inputMap.put(KeyStroke.getKeyStroke("+"), "increaseSize");
        actionMap.put("increaseSize", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeComponentSize(1.1f); // Increase size by 10%
            }
        });

        // Bind Minus to decrease size
        inputMap.put(KeyStroke.getKeyStroke("-"), "decreaseSize");
        actionMap.put("decreaseSize", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeComponentSize(0.9f); // Decrease size by 10%
            }
        });

        // Mouse wheel listener for resizing
        component.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                changeComponentSize(1.1f); // Scroll up to increase size
            } else {
                changeComponentSize(0.9f); // Scroll down to decrease size
            }
        });
    }
    private void changeComponentSize(float scale) {
        Font currentFont = getFont();
        int newSize = Math.round(currentFont.getSize() * scale);

        if (newSize > Constants.MAX_FONT_SIZE) {
            newSize = Constants.MAX_FONT_SIZE;
        } else if (newSize < Constants.MIN_FONT_SIZE) {
            newSize = Constants.MIN_FONT_SIZE;
        }

        // Update the font for all components in your UI
        setFontRecursively(this, newSize);
    }
    private void setFontRecursively(Component component, int fontSize) {
        Font currentFont = component.getFont();
        component.setFont(currentFont.deriveFont((float) fontSize));

        // Adjust size for JTextField specifically
        if (component instanceof JTextField textField) {
            Dimension newSize = new Dimension(150, Math.max(30, Math.round(fontSize * 1.5f))); // Maintain a minimum height
            textField.setPreferredSize(newSize);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setFontRecursively(child, fontSize);
            }
        }
    }

    private void onFontSizeChange(FontSize fontSize) {
        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), fontSize.getSize());
        utils.updateUIFont(newFont);
    }
    private void onFontStyleChange(FontStyle fontStyle) {
        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getName(), fontStyle.getStyle(), currentFont.getSize());
        utils.updateUIFont(newFont);
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
        lengthUnitComboBox.setSelectedIndex(1);
        heightUnitComboBox = new JComboBox<>(unitsList);
        heightUnitComboBox.setSelectedIndex(1);
    }
    private void initializeComboBox() {
        itemMap = new HashMap<>();
        Item[] items = productsUtils.getAllItems();

        if (items == null || items.length == 0) {
            System.out.println("No items available.");
            itemComboBox = new JComboBox<>();
            return;
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
        saveButton = new MyButton("حفظ");
        saveButton.addActionListener(e -> saveToExcel());

        addButton = new MyButton("إضافة");
        addButton.addActionListener(e -> addProduct());

        displayButton = new MyButton("عرض الكل");
        displayButton.addActionListener(e -> displayAllEntries());
    }


    private void addComponents(GridBagConstraints gbc, JPanel panel) {
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("الصنف"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("العدد"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("الطول"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(lengthField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(lengthUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("الارتفاع"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(heightField, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(heightUnitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("المسطح"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(surfaceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        panel.add(new JLabel("سعر المتر"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        panel.add(priceField, gbc);

        // Button panel setup
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END; // right
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(addButton, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START; // left
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(displayButton, gbc);
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
            double length = parseLength(lengthField.getText(), Unit.fromLabel((String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem())));
            double height = parseHeight(heightField.getText(), Unit.fromLabel((String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem())));
            double surface = quantity * length * height;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            surfaceField.setText(decimalFormat.format(surface));
        } catch (NumberFormatException e) {
            surfaceField.setText("");
        }
    }

    private double parseLength(String lengthText, Unit unit) {
        double length = Double.parseDouble(lengthText);
        return unit.getLabel().equals("سم") ? length / 100 : length; // Convert cm to m
    }
    private double parseHeight(String heightText, Unit unit) {
        double height = Double.parseDouble(heightText);
        return unit.getLabel().equals("سم") ? height / 100 : height; // Convert cm to m
    }

    private void addProduct() {
        try {
            String selectedName = (String) itemComboBox.getSelectedItem();
            double quantity = Double.parseDouble(quantityField.getText());
            double length = parseLength(lengthField.getText(), Unit.fromLabel((String) Objects.requireNonNull(lengthUnitComboBox.getSelectedItem())));
            double height = parseHeight(heightField.getText(), Unit.fromLabel((String) Objects.requireNonNull(heightUnitComboBox.getSelectedItem())));
            double surface = quantity * length * height;
            double price = Double.parseDouble(priceField.getText());
            double total = quantity * price;

            productEntries.add(new ProductEntry(selectedName, quantity, length, height, surface, price, total));
            utils.showDialog("تمت إضافه الصنف "+ selectedName);
            updateTableModel();
            clearFields();
        } catch (NumberFormatException e) {
            utils.showError("الرجاء ادخال رقم...");
        }
    }

    private void displayAllEntries() {
        JDialog dialog = new JDialog(this, "كل الأصناف", false); // Non-modal dialog
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
        MyButton editButton = new MyButton("تعديل");
        MyButton removeSelectedButton = new MyButton("حذف");
        MyButton removeAllButton = new MyButton("حذف الكل");
        MyButton okButton = new MyButton("حسنا");
        MyButton moveUpButton = new MyButton("تحريك للأعلى");
        MyButton moveDownButton = new MyButton("تحريك للأسفل");

        editButton.addActionListener(e -> editSelectedProduct(table));
        removeSelectedButton.addActionListener(e -> removeSelectedProduct(table));
        removeAllButton.addActionListener(e -> removeAllProducts());
        okButton.addActionListener(e -> ((JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, okButton)).dispose());

        moveUpButton.addActionListener(e -> moveSelectedUp(table));
        moveDownButton.addActionListener(e -> moveSelectedDown(table));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(removeAllButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        buttonPanel.add(okButton);
        return buttonPanel;
    }

    private void moveSelectedUp(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow > 0) {
            // Move the selected row up
            moveRow(table, selectedRow, selectedRow - 1);
            table.clearSelection();
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }

    private void moveSelectedDown(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < table.getRowCount() - 1) {
            // Move the selected row down
            moveRow(table, selectedRow, selectedRow + 1);
            table.clearSelection();
            table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }

    private void moveRow(JTable table, int fromIndex, int toIndex) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Get the row data
        Object[] rowData = new Object[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            rowData[i] = model.getValueAt(fromIndex, i);
        }

        // Remove the row from the original position
        model.removeRow(fromIndex);

        // Insert the row at the new position
        model.insertRow(toIndex, rowData);

        // Update productEntries list
        ProductEntry entry = productEntries.remove(fromIndex);
        productEntries.add(toIndex, entry);

        // Notify the model that the data has changed
        model.fireTableDataChanged();
    }

    private void editSelectedProduct(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            ProductEntry selectedEntry = productEntries.get(selectedRow);
            showEditDialog(selectedEntry);
        } else {
            utils.showDialog("الرجاء إختيار الصنف اولا..");
        }
    }
    private void showEditDialog(ProductEntry entry) {
        JDialog editDialog = new JDialog(this, "تعديل الصنف", true);
        JPanel editPanel = new JPanel(new GridLayout(0, 2));

        JComboBox<String> editItemComboBox = new JComboBox<>(itemMap.keySet().toArray(new String[0]));
        editItemComboBox.setSelectedItem(entry.getItemName());

        JTextField editQuantityField = new JTextField(String.valueOf(entry.getQuantity()));
        JTextField editLengthField = new JTextField(String.valueOf(entry.getLength()));


        JComboBox<String> editLengthUnitComboBox = new JComboBox<>(unitsList);
        editLengthUnitComboBox.setSelectedItem(entry.getLength() < 1 ? "سم" : 1);

        JTextField editHeightField = new JTextField(String.valueOf(entry.getHeight()));
        JComboBox<String> editHeightUnitComboBox = new JComboBox<>(unitsList);
        editHeightUnitComboBox.setSelectedItem(entry.getHeight() < 1 ? "سم" : 1);

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
                updateSurfaceField();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSurfaceField();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSurfaceField();
            }
        };

        editQuantityField.getDocument().addDocumentListener(surfaceListener);
        editLengthField.getDocument().addDocumentListener(surfaceListener);
        editHeightField.getDocument().addDocumentListener(surfaceListener);

        // Create button panel for save/cancel actions
        MyButton saveEditButton = new MyButton("حفظ");
        MyButton cancelButton = new MyButton("إالغاء");

        saveEditButton.addActionListener(e -> {
            try {
                double quantity = Double.parseDouble(editQuantityField.getText());
                double length = parseLength(editLengthField.getText(), Unit.fromLabel((String) Objects.requireNonNull(editLengthUnitComboBox.getSelectedItem())));
                double height = parseHeight(editHeightField.getText(), Unit.fromLabel((String) Objects.requireNonNull(editHeightUnitComboBox.getSelectedItem())));
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
                utils.showError("الرجاء ادخال رقم...");
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());


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

    private void removeSelectedProduct(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            productEntries.remove(selectedRow);
            updateTableModel();
        } else {
            utils.showDialog("الرجاء تحديد الصنف اولا....");
        }
    }

    private void removeAllProducts() {
        int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد أنك تريد إزالة كل الأصناف؟", "يتأكد", JOptionPane.YES_NO_OPTION);
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
        String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date());

        File outputFile = new File(documentsPath, dateString + ".xlsx");


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
                utils.showError("خطأ في حفظ البيانات إلى ملف الإكسل: " + e.getMessage());
            }
        } catch (IOException e) {
            utils.showError("خطأ في إنشاء مساحه ملف الإكسل: " + e.getMessage());
        }
    }
    private void showSaveDialog(File outputFile) throws IOException {
        Object[] options = {
                "فتح الملف",
                "فتح موقع الملف",
                "إغلاق"};

        int choice = JOptionPane.showOptionDialog(
                null,
                "تم حفظ الملف في " + outputFile.getAbsolutePath(),
                "تم حفظ الملف",
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

    private File chooseExcelFile() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("", "xls", "xlsx");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }
    private void importFromExcel(File inputFile) {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            productEntries.clear();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    ProductEntry entry = new ProductEntry(
                            row.getCell(Constants.ITEM_COLUMN).getStringCellValue(),
                            row.getCell(Constants.QUANTITY_COLUMN).getNumericCellValue(),
                            row.getCell(Constants.LENGTH_COLUMN).getNumericCellValue(),
                            row.getCell(Constants.HEIGHT_COLUMN).getNumericCellValue(),
                            row.getCell(Constants.SURFACE_COLUMN).getNumericCellValue(),
                            row.getCell(Constants.PRICE_COLUMN).getNumericCellValue(),
                            row.getCell(Constants.TOTAL_COLUMN).getNumericCellValue()
                    );
                    productEntries.add(entry);
                }
            }

            // Notify user of successful import
            utils.showInfo("تم استيراد البيانات بنجاح من ملف الإكسل.");
        } catch (IOException e) {
            utils.showError("خطأ في استيراد البيانات من ملف الإكسل: " + e.getMessage());
        } catch (Exception e) {
            utils.showError("خطأ غير متوقع: " + e.getMessage());
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