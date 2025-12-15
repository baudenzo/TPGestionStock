package modele;

import java.sql.*;

public class EntrepriseDAO {

    public Entreprise authentifierEntreprise(String codeEntreprise, String nomUtilisateur, String motDePasse) {
        String sql = "SELECT e.*, u.id_utilisateur, u.nom_utilisateur, u.est_fournisseur " +
                "FROM entreprises e " +
                "JOIN utilisateurs_externes u ON e.id_entreprise = u.id_entreprise " +
                "WHERE e.code_entreprise = ? AND u.nom_utilisateur = ? AND u.mot_de_passe = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codeEntreprise);
            stmt.setString(2, nomUtilisateur);
            stmt.setString(3, motDePasse);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Entreprise entreprise = new Entreprise();
                entreprise.setIdEntreprise(rs.getInt("id_entreprise"));
                entreprise.setNomEntreprise(rs.getString("nom_entreprise"));
                entreprise.setCodeEntreprise(rs.getString("code_entreprise"));
                entreprise.setAdresse(rs.getString("adresse"));
                entreprise.setTelephone(rs.getString("telephone"));
                entreprise.setEmail(rs.getString("email"));
                return entreprise;
            }
        } catch (SQLException e) {
            System.err.println("Erreur authentification entreprise : " + e.getMessage());
        }
        return null;
    }

    public Entreprise getEntrepriseParId(int idEntreprise) {
        String sql = "SELECT * FROM entreprises WHERE id_entreprise = ?";

        try (Connection conn = DatabaseConnectionExterne.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntreprise);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Entreprise entreprise = new Entreprise();
                entreprise.setIdEntreprise(rs.getInt("id_entreprise"));
                entreprise.setNomEntreprise(rs.getString("nom_entreprise"));
                entreprise.setCodeEntreprise(rs.getString("code_entreprise"));
                entreprise.setAdresse(rs.getString("adresse"));
                entreprise.setTelephone(rs.getString("telephone"));
                entreprise.setEmail(rs.getString("email"));
                return entreprise;
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération entreprise : " + e.getMessage());
        }
        return null;
    }
}