package modele;

import java.sql.*;

public class DatabaseConnectionExterne {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_stock_externe";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données EXTERNE réussie");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion EXTERNE : " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion EXTERNE fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture EXTERNE : " + e.getMessage());
        }
    }
}