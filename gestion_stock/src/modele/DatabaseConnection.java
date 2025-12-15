package modele;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://192.168.23.102:3306/gestion_stock";
    private static final String USER = "enzob";
    private static final String PASSWORD = "eleve";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données réussie");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion : " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}