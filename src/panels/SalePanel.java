package panels;

import daos.ClientDAO;
import daos.MedicationDAO;
import daos.SaleDAO;
import models.Client;
import models.Medication;
import models.Sale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SalePanel extends JPanel {
    private final SaleDAO dao = new SaleDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final MedicationDAO medDAO = new MedicationDAO();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JComboBox<Client> clientCombo = new JComboBox<>();
    private final JComboBox<Medication> medCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());
    private final JTextField quantityField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JComboBox<String> paymentCombo = new JComboBox<>(new String[] { "кеш", "карта", "здравна каса" });
    private final JTextField searchField = new JTextField();

    private final JButton saveButton = new JButton("Запази");
    private final JButton deleteButton = new JButton("Изтрий");
    private final JButton clearButton = new JButton("Изчисти");
    private final JButton searchButton = new JButton("Търси");

    private int selectedId = -1;

    public SalePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadComboData();

        String[] columns = { "ID", "Клиент", "Лекарство", "Дата", "Бройки", "Сума", "Плащане" };
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
        formPanel.add(new JLabel("Клиент:"));
        formPanel.add(clientCombo);
        formPanel.add(new JLabel("Лекарство:"));
        formPanel.add(medCombo);
        formPanel.add(new JLabel("Дата (yyyy-MM-dd):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Бройки:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Обща сума:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Начин на плащане:"));
        formPanel.add(paymentCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.add(new JLabel("Търси по клиент:"), BorderLayout.WEST);
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

    private void loadComboData() {
        clientCombo.removeAllItems();
        for (Client c : clientDAO.getAll())
            clientCombo.addItem(c);
        medCombo.removeAllItems();
        for (Medication m : medDAO.getAll())
            medCombo.addItem(m);
    }

    private void loadAll() {
        fillTable(dao.getAll());
    }

    private void search() {
        String kw = searchField.getText().trim();
        fillTable(kw.isEmpty() ? dao.getAll() : dao.searchByClientName(kw));
    }

    private void fillTable(List<Sale> list) {
        tableModel.setRowCount(0);
        for (Sale s : list) {
            tableModel.addRow(new Object[] {
                    s.getId(), s.getClientName(), s.getMedicationName(),
                    s.getSaleDate().toString(), s.getQuantity(),
                    String.format("%.2f", s.getTotalPrice()),
                    s.getPaymentMethod()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        dateField.setText(tableModel.getValueAt(row, 3).toString());
        quantityField.setText(tableModel.getValueAt(row, 4).toString());
        priceField.setText(tableModel.getValueAt(row, 5).toString());
        String payment = (String) tableModel.getValueAt(row, 6);
        for (int i = 0; i < paymentCombo.getItemCount(); i++) {
            if (paymentCombo.getItemAt(i).equals(payment)) {
                paymentCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void save() {
        String qStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();
        String dateStr = dateField.getText().trim();

        if (qStr.isEmpty() || priceStr.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Попълни всички задължителни полета!", "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qStr);
            double price = Double.parseDouble(priceStr);
            LocalDate date = LocalDate.parse(dateStr);
            Client c = (Client) clientCombo.getSelectedItem();
            Medication m = (Medication) medCombo.getSelectedItem();

            Sale s = new Sale();
            s.setClientId(c.getId());
            s.setMedicationId(m.getId());
            s.setSaleDate(date);
            s.setQuantity(qty);
            s.setTotalPrice(price);
            s.setPaymentMethod((String) paymentCombo.getSelectedItem());

            if (selectedId == -1)
                dao.insert(s);
            else {
                s.setId(selectedId);
                dao.update(s);
            }

            loadAll();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Провери формата на датата и числата!", "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Избери продажба от таблицата!", "Грешка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Искаш ли да изтриеш тази продажба?", "Потвърждение",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.delete(selectedId);
            loadAll();
            clearForm();
        }
    }

    private void clearForm() {
        selectedId = -1;
        dateField.setText(LocalDate.now().toString());
        quantityField.setText("");
        priceField.setText("");
        if (clientCombo.getItemCount() > 0)
            clientCombo.setSelectedIndex(0);
        if (medCombo.getItemCount() > 0)
            medCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
