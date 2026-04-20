package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:./pharmacy_db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initDatabase() {
        File sqlFile = new File("src/resources/schema.sql");
        if (!sqlFile.exists())
            return;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader reader = new BufferedReader(new FileReader(sqlFile))) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("--"))
                    continue;

                sb.append(line);

                if (line.contains(";")) {
                    stmt.execute(sb.toString());
                    sb.setLength(0);
                }
            }
            System.out.println("Базата е готова!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isDatabaseAlreadyInitialized() {
        try (Connection conn = getConnection();
                ResultSet rs = conn.getMetaData().getTables(null, null, "CATEGORIES", null)) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}