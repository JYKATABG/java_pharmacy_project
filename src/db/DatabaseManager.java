package db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:h2:mem:hoteldb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initDatabase() throws SQLException, IOException {
        String path = DatabaseManager.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        File sqlFile = new File("src/resources/schema.sql");
        String sql = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()));

        try (Statement stmt = getConnection().createStatement()) {
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
        }
    }
}