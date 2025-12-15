package modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReapprovisionnementDAO {

    public boolean creerReapprovisionnement(Reapprovisionnement reappro) {
        String sql = "INSERT INTO reapprovisionnements (id_produit_externe, quantite_commandee, " +
                "date_livraison_prevue, stock_au_moment_commande, consommation_estimee) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reappro.getIdProduitExterne());
            stmt.setInt(2, reappro.getQuantiteCommandee());
            stmt.setTimestamp(3, Timestamp.valueOf(reappro.getDateLivraisonPrevue()));
            stmt.setInt(4, reappro.getStockAuMomentCommande());
            stmt.setDouble(5, reappro.getConsommationEstimee());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reappro.setIdReappro(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création réapprovisionnement : " + e.getMessage());
        }
        return false;
    }

    public List<Reapprovisionnement> listerReapprosEnCours(int idEntreprise) {
        List<Reapprovisionnement> reappros = new ArrayList<>();
        String sql = "SELECT r.* FROM reapprovisionnements r " +
                "JOIN produits_externes p ON r.id_produit_externe = p.id_produit " +
                "WHERE p.id_entreprise = ? AND r.statut = 'EN_COURS' " +
                "ORDER BY r.date_livraison_prevue";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntreprise);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reappros.add(mapResultSetToReappro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage réappros : " + e.getMessage());
        }
        return reappros;
    }

    public boolean marquerLivre(int idReappro) {
        String sql = "UPDATE reapprovisionnements SET statut = 'LIVRE', " +
                "date_livraison_reelle = CURRENT_TIMESTAMP WHERE id_reappro = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReappro);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur marquage livraison : " + e.getMessage());
            return false;
        }
    }

    public Reapprovisionnement getReapproParId(int idReappro) {
        String sql = "SELECT * FROM reapprovisionnements WHERE id_reappro = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReappro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReappro(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération réappro : " + e.getMessage());
        }
        return null;
    }

    private Reapprovisionnement mapResultSetToReappro(ResultSet rs) throws SQLException {
        Reapprovisionnement reappro = new Reapprovisionnement();
        reappro.setIdReappro(rs.getInt("id_reappro"));
        reappro.setIdProduitExterne(rs.getInt("id_produit_externe"));
        reappro.setQuantiteCommandee(rs.getInt("quantite_commandee"));
        reappro.setDateCommande(rs.getTimestamp("date_commande").toLocalDateTime());
        reappro.setDateLivraisonPrevue(rs.getTimestamp("date_livraison_prevue").toLocalDateTime());

        Timestamp livraisonReelle = rs.getTimestamp("date_livraison_reelle");
        if (livraisonReelle != null) {
            reappro.setDateLivraisonReelle(livraisonReelle.toLocalDateTime());
        }

        reappro.setStatut(rs.getString("statut"));
        reappro.setStockAuMomentCommande(rs.getInt("stock_au_moment_commande"));
        reappro.setConsommationEstimee(rs.getDouble("consommation_estimee"));
        return reappro;
    }
}