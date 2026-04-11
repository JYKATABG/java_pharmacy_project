package panels;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Аптека - Управление");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Лекарства", new MedicationPanel());
        tabs.addTab("Клиенти", new ClientPanel());
        tabs.addTab("Продажби", new SalePanel());

        add(tabs);
    }
}
