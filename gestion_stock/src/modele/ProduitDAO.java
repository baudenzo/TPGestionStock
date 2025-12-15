package modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public boolean creerProduit(Produit produit) {
        String sql = "INSERT INTO produits (code_produit, description_produit, stock, " +
                "stock_securite, stock_maximum) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produit.getCodeProduit());
            stmt.setString(2, produit.getDescriptionProduit());
            stmt.setInt(3, produit.getStock());
            stmt.setInt(4, produit.getStockSecurite());
            stmt.setInt(5, produit.getStockMaximum());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur création produit : " + e.getMessage());
            return false;
        }
    }

    public List<Produit> listerProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits ORDER BY code_produit";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage produits : " + e.getMessage());
        }
        return produits;
    }

    public boolean mettreAJourStock(int idProduit, int nouvelleQuantite) {
        String sql = "UPDATE produits SET stock = ? WHERE id_produit = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, idProduit);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour stock : " + e.getMessage());
            return false;
        }
    }

    public Produit getProduitParId(int idProduit) {
        String sql = "SELECT * FROM produits WHERE id_produit = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduit);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération produit : " + e.getMessage());
        }
        return null;
    }

    public Produit getProduitParCode(String codeProduit) {
        String sql = "SELECT * FROM produits WHERE code_produit = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codeProduit);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération produit par code : " + e.getMessage());
        }
        return null;
    }

    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setIdProduit(rs.getInt("id_produit"));
        produit.setCodeProduit(rs.getString("code_produit"));
        produit.setDescriptionProduit(rs.getString("description_produit"));
        produit.setStock(rs.getInt("stock"));
        produit.setStockSecurite(rs.getInt("stock_securite"));
        produit.setStockMaximum(rs.getInt("stock_maximum"));
        produit.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        produit.setDateMiseAJour(rs.getTimestamp("date_mise_a_jour").toLocalDateTime());
        return produit;
    }
}