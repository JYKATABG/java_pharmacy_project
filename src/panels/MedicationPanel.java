package panels;

import daos.CategoryDAO;
import daos.MedicationDAO;
import models.Category;
import models.Medication;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicationPanel extends JPanel {
    private final MedicationDAO dao = new MedicationDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField nameField = new JTextField();
    private final JTextField manufacturerField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JCheckBox prescriptionBox = new JCheckBox("Изисква рецепта");
    private final JComboBox<Category> categoryCombo = new JComboBox<>();
    private final JTextField searchField = new JTextField();

    private final JButton saveButton = new JButton("Запази");
    private final JButton deleteButton = new JButton("Изтрий");
    private final JButton clearButton = new JButton("Изчисти");
    private final JButton searchButton = new JButton("Търси");

    private int selectedId = -1;

    public MedicationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadCategories();

        String[] columns = { "ID", "Наименование", "Производител", "Цена", "Наличност", "Рецепта", "Категория" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                fillFormFromTable();
        });

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.add(new JLabel("Наименование:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Производител:"));
        formPanel.add(manufacturerField);
        formPanel.add(new JLabel("Цена:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Наличност:"));
        formPanel.add(stockField);
        formPanel.add(new JLabel("Категория:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(prescriptionBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.add(new JLabel("Търси по име:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);

        saveButton.addActionListener(e -> save());
        deleteButton.addActionListener(e -> delete());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> search());

        loadAll();
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        for (Category c : categoryDAO.getAll())
            categoryCombo.addItem(c);
    }

    private void loadAll() {
        fillTable(dao.getAll());
    }

    private void search() {
        String kw = searchField.getText().trim();
        fillTable(kw.isEmpty() ? dao.getAll() : dao.searchByName(kw));
    }

    private void fillTable(List<Medication> list) {
        tableModel.setRowCount(0);
        for (Medication m : list) {
            tableModel.addRow(new Object[] {
                    m.getId(), m.getName(), m.getManufacturer(),
                    String.format("%.2f", m.getPrice()),
                    m.getStock(),
                    m.isRequiresPrescription() ? "Да" : "Не",
                    m.getCategoryName()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        manufacturerField.setText((String) tableModel.getValueAt(row, 2));
        priceField.setText(tableModel.getValueAt(row, 3).toString());
        stockField.setText(tableModel.getValueAt(row, 4).toString());
        prescriptionBox.setSelected(tableModel.getValueAt(row, 5).equals("Да"));
        String catName = (String) tableModel.getValueAt(row, 6);
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getCategoryName().equals(catName)) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void save() {
        String name = nameField.getText().trim();
        String mfr = manufacturerField.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();

        if (name.isEmpty() || mfr.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Попълни всички задължителни полета!", "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            Category cat = (Category) categoryCombo.getSelectedItem();

            Medication m = new Medication();
            m.setName(name);
            m.setManufacturer(mfr);
            m.setPrice(price);
            m.setStock(stock);
            m.setRequiresPrescription(prescriptionBox.isSelected());
            m.setCategoryId(cat.getId());

            if (selectedId == -1)
                dao.insert(m);
            else {
                m.setId(selectedId);
                dao.update(m);
            }

            loadAll();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Цената и наличността трябва да са числа!", "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Избери лекарство от таблицата!", "Грешка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Искаш ли да изтриеш това лекарство?", "Потвърждание",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dao.delete(selectedId);
            loadAll();
            clearForm();
        }
    }

    private void clearForm() {
        selectedId = -1;
        nameField.setText("");
        manufacturerField.setText("");
        priceField.setText("");
        stockField.setText("");
        prescriptionBox.setSelected(false);
        if (categoryCombo.getItemCount() > 0)
            categoryCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
