package vue;

import controleur.AuthentificationControleur;
import modele.Utilisateur;
import javax.swing.*;
import java.awt.*;

public class VueConnexion extends JFrame {
    private JTextField champUtilisateur;
    private JPasswordField champMotDePasse;
    private JButton boutonConnexion;
    private AuthentificationControleur controleur;

    public VueConnexion() {
        controleur = new AuthentificationControleur();
        initialiserInterface();
    }

    private void initialiserInterface() {
        setTitle("Gestion de Stock - Connexion");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Connexion", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        panelPrincipal.add(titre, BorderLayout.NORTH);

        JPanel panelFormulaire = new JPanel(new GridLayout(2, 2, 10, 10));

        panelFormulaire.add(new JLabel("Utilisateur :"));
        champUtilisateur = new JTextField();
        panelFormulaire.add(champUtilisateur);

        panelFormulaire.add(new JLabel("Mot de passe :"));
        champMotDePasse = new JPasswordField();
        panelFormulaire.add(champMotDePasse);

        panelPrincipal.add(panelFormulaire, BorderLayout.CENTER);

        boutonConnexion = new JButton("Se connecter");
        boutonConnexion.setFont(new Font("Arial", Font.BOLD, 14));
        boutonConnexion.addActionListener(e -> seConnecter());

        champMotDePasse.addActionListener(e -> seConnecter());

        panelPrincipal.add(boutonConnexion, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void seConnecter() {
        String nomUtilisateur = champUtilisateur.getText();
        String motDePasse = new String(champMotDePasse.getPassword());

        Utilisateur utilisateur = controleur.connecter(nomUtilisateur, motDePasse);

        if (utilisateur != null) {
            JOptionPane.showMessageDialog(this,
                    "Bienvenue " + utilisateur.getNomUtilisateur() + " !",
                    "Connexion réussie", JOptionPane.INFORMATION_MESSAGE);

            new VueGestionStock(utilisateur).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Identifiants incorrects !",
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            champMotDePasse.setText("");
        }
    }
}