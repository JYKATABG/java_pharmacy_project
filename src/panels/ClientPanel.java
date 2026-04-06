package panels;

import daos.ClientDAO;
import models.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ClientPanel extends JPanel {
    private final ClientDAO dao = new ClientDAO();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField birthDateField = new JTextField();
    private final JTextField searchField = new JTextField();

    private final JButton saveButton = new JButton("Запази");
    private final JButton deleteButton = new JButton("Изтрий");
    private final JButton clearButton = new JButton("Изчисти");
    private final JButton searchButton = new JButton("Търси");

    private int selectedId = -1;

    public ClientPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = { "ID", "Име", "Фамилия", "Телефон", "Имейл", "Рождена дата" };
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
        formPanel.setPreferredSize(new Dimension(290, 0));
        formPanel.add(new JLabel("Ime:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Familiya:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Telefon:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Iмейл:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Rozhd. data (yyyy-MM-dd):"));
        formPanel.add(birthDateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.add(new JLabel("Tyrsi po familiya:"), BorderLayout.WEST);
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

    private void loadAll() {
        fillTable(dao.getAll());
    }

    private void search() {
        String kw = searchField.getText().trim();
        fillTable(kw.isEmpty() ? dao.getAll() : dao.searchByLastName(kw));
    }

    private void fillTable(List<Client> list) {
        tableModel.setRowCount(0);
        for (Client c : list) {
            tableModel.addRow(new Object[] {
                    c.getId(), c.getFirstName(), c.getLastName(),
                    c.getPhone(), c.getEmail(),
                    c.getBirthDate() != null ? c.getBirthDate().toString() : ""
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        firstNameField.setText((String) tableModel.getValueAt(row, 1));
        lastNameField.setText((String) tableModel.getValueAt(row, 2));
        phoneField.setText((String) tableModel.getValueAt(row, 3));
        emailField.setText((String) tableModel.getValueAt(row, 4));
        birthDateField.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void save() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ime, familiya i telefon sa zadylzhitelni!", "Greshka",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client c = new Client();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setPhone(phone);
        c.setEmail(emailField.getText().trim());

        String bdStr = birthDateField.getText().trim();
        if (!bdStr.isEmpty()) {
            try {
                c.setBirthDate(LocalDate.parse(bdStr));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datata tryabva da e v format yyyy-MM-dd!", "Greshka",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (selectedId == -1)
            dao.insert(c);
        else {
            c.setId(selectedId);
            dao.update(c);
        }

        loadAll();
        clearForm();
    }

    private void delete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Izberi klient ot tablicata!", "Greshka", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Iztrii tozi klient?", "Potvyrdi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.delete(selectedId);
            loadAll();
            clearForm();
        }
    }

    private void clearForm() {
        selectedId = -1;
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        birthDateField.setText("");
        table.clearSelection();
    }
}
