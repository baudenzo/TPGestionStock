package modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitExterneDAO {

    public boolean creerOuMettreAJourProduit(ProduitExterne p) {
        String selectSql = "SELECT id_produit FROM produits_externes WHERE id_entreprise = ? AND code_produit = ?";
        String insertSql = "INSERT INTO produits_externes (id_entreprise, code_produit, description_produit, stock_actuel, stock_securite, stock_maximum, consommation_journaliere, delai_livraison_jours, date_mise_a_jour) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String updateSql = "UPDATE produits_externes SET description_produit = ?, stock_actuel = ?, stock_securite = ?, stock_maximum = ?, consommation_journaliere = ?, delai_livraison_jours = ?, date_mise_a_jour = CURRENT_TIMESTAMP WHERE id_produit = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection()) {
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, p.getIdEntreprise());
            selectStmt.setString(2, p.getCodeProduit());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int idProduit = rs.getInt("id_produit");
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, p.getDescriptionProduit());
                updateStmt.setInt(2, p.getStockActuel());
                updateStmt.setInt(3, p.getStockSecurite());
                updateStmt.setInt(4, p.getStockMaximum());
                updateStmt.setDouble(5, p.getConsommationJournaliere());
                updateStmt.setInt(6, p.getDelaiLivraisonJours());
                updateStmt.setInt(7, idProduit);
                updateStmt.executeUpdate();
            } else {
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setInt(1, p.getIdEntreprise());
                insertStmt.setString(2, p.getCodeProduit());
                insertStmt.setString(3, p.getDescriptionProduit());
                insertStmt.setInt(4, p.getStockActuel());
                insertStmt.setInt(5, p.getStockSecurite());
                insertStmt.setInt(6, p.getStockMaximum());
                insertStmt.setDouble(7, p.getConsommationJournaliere());
                insertStmt.setInt(8, p.getDelaiLivraisonJours());
                int affected = insertStmt.executeUpdate();
                if (affected > 0) {
                    ResultSet gk = insertStmt.getGeneratedKeys();
                    if (gk.next()) {
                        p.setIdProduit(gk.getInt(1));
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Erreur création/mise à jour produit externe : " + e.getMessage());
            return false;
        }
    }

    public List<ProduitExterne> listerProduitsParEntreprise(int idEntreprise) {
        List<ProduitExterne> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits_externes WHERE id_entreprise = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntreprise);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProduitExterne p = new ProduitExterne();
                p.setIdProduit(rs.getInt("id_produit"));
                p.setIdEntreprise(rs.getInt("id_entreprise"));
                p.setCodeProduit(rs.getString("code_produit"));
                p.setDescriptionProduit(rs.getString("description_produit"));
                p.setStockActuel(rs.getInt("stock_actuel"));
                p.setStockSecurite(rs.getInt("stock_securite"));
                p.setStockMaximum(rs.getInt("stock_maximum"));
                p.setConsommationJournaliere(rs.getDouble("consommation_journaliere"));
                p.setDelaiLivraisonJours(rs.getInt("delai_livraison_jours"));
                produits.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage produits externes : " + e.getMessage());
        }

        return produits;
    }
}
