import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import db.DatabaseManager;
import panels.MainFrame;

public class App {
    public static void main(String[] args) throws Exception {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        DatabaseManager.initDatabase();

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
