package controleur;

import modele.Utilisateur;
import modele.UtilisateurDAO;

public class AuthentificationControleur {
    private UtilisateurDAO utilisateurDAO;

    public AuthentificationControleur() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public Utilisateur connecter(String nomUtilisateur, String motDePasse) {
        if (nomUtilisateur == null || nomUtilisateur.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            return null;
        }
        return utilisateurDAO.authentifier(nomUtilisateur, motDePasse);
    }
}