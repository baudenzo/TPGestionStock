package vue;

import controleur.GestionStockControleur;
import modele.Produit;
import modele.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueGestionStock extends JFrame {
    private Utilisateur utilisateurConnecte;
    private GestionStockControleur controleur;

    // Composants gauche (formulaire)
    private JTextField champCodeProduit, champDescription, champStock,
            champStockSecurite, champStockMaximum;
    private JButton boutonCreer, boutonRafraichir;

    // Composants droite (liste)
    private JTable tableProduits;
    private DefaultTableModel modeleTable;
    private JTextField champVariation;
    private JButton boutonAjouter, boutonRetirer;

    public VueGestionStock(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.controleur = new GestionStockControleur();
        initialiserInterface();
        chargerProduits();
    }

    private void initialiserInterface() {
        setTitle("Gestion de Stock - " + utilisateurConnecte.getNomUtilisateur() +
                (utilisateurConnecte.isEstAdmin() ? " (Administrateur)" : ""));
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal avec split horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);

        // PARTIE GAUCHE - Formulaire
        JPanel panelGauche = creerPanelFormulaire();
        splitPane.setLeftComponent(panelGauche);

        // PARTIE DROITE - Liste des produits
        JPanel panelDroit = creerPanelListe();
        splitPane.setRightComponent(panelDroit);

        add(splitPane);
    }

    private JPanel creerPanelFormulaire() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titre = new JLabel("Création de produit", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titre, BorderLayout.NORTH);

        // Formulaire
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));

        panelForm.add(new JLabel("Code produit * :"));
        champCodeProduit = new JTextField();
        panelForm.add(champCodeProduit);

        panelForm.add(new JLabel("Description * :"));
        champDescription = new JTextField();
        panelForm.add(champDescription);

        panelForm.add(new JLabel("Stock * :"));
        champStock = new JTextField("0");
        panelForm.add(champStock);

        panelForm.add(new JLabel("Stock sécurité :"));
        champStockSecurite = new JTextField("0");
        panelForm.add(champStockSecurite);

        panelForm.add(new JLabel("Stock maximum * :"));
        champStockMaximum = new JTextField();
        panelForm.add(champStockMaximum);

        panel.add(panelForm, BorderLayout.CENTER);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        boutonCreer = new JButton("Créer le produit");
        boutonCreer.setEnabled(utilisateurConnecte.isEstAdmin());
        boutonCreer.addActionListener(e -> creerProduit());
        panelBoutons.add(boutonCreer);

        if (!utilisateurConnecte.isEstAdmin()) {
            JLabel avertissement = new JLabel("(Réservé aux administrateurs)");
            avertissement.setForeground(Color.RED);
            panelBoutons.add(avertissement);
        }

        panel.add(panelBoutons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creerPanelListe() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titre = new JLabel("Liste des produits", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titre, BorderLayout.NORTH);

        // Table
        String[] colonnes = { "ID", "Code", "Description", "Stock", "Sécurité", "Maximum" };
        modeleTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableProduits = new JTable(modeleTable);
        tableProduits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProduits.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableProduits.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableProduits.getColumnModel().getColumn(2).setPreferredWidth(200);

        // Colorisation du stock
        tableProduits.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 3) { // Colonne Stock
                    int stock = (int) table.getValueAt(row, 3);
                    int stockSecurite = (int) table.getValueAt(row, 4);

                    if (stock < stockSecurite) {
                        c.setBackground(new Color(255, 100, 100)); // Rouge
                    } else if (stock < stockSecurite + 10) {
                        c.setBackground(new Color(255, 165, 0)); // Orange
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableProduits);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de gestion du stock
        JPanel panelGestion = new JPanel(new FlowLayout());

        panelGestion.add(new JLabel("Quantité :"));
        champVariation = new JTextField(5);
        panelGestion.add(champVariation);

        boutonAjouter = new JButton("➕ Ajouter");
        boutonAjouter.addActionListener(e -> modifierStock(true));
        panelGestion.add(boutonAjouter);

        boutonRetirer = new JButton("➖ Retirer");
        boutonRetirer.addActionListener(e -> modifierStock(false));
        panelGestion.add(boutonRetirer);

        boutonRafraichir = new JButton("🔄 Rafraîchir");
        boutonRafraichir.addActionListener(e -> chargerProduits());
        panelGestion.add(boutonRafraichir);

        panel.add(panelGestion, BorderLayout.SOUTH);

        return panel;
    }

    private void creerProduit() {
        String erreur = controleur.creerProduit(
                champCodeProduit.getText(),
                champDescription.getText(),
                champStock.getText(),
                champStockSecurite.getText(),
                champStockMaximum.getText());

        if (erreur == null) {
            JOptionPane.showMessageDialog(this,
                    "Produit créé avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Vider les champs
            champCodeProduit.setText("");
            champDescription.setText("");
            champStock.setText("0");
            champStockSecurite.setText("0");
            champStockMaximum.setText("");

            chargerProduits();
        } else {
            JOptionPane.showMessageDialog(this,
                    erreur,
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierStock(boolean ajouter) {
        int ligneSelectionnee = tableProduits.getSelectedRow();

        if (ligneSelectionnee == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit",
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int quantite = Integer.parseInt(champVariation.getText());
            int idProduit = (int) modeleTable.getValueAt(ligneSelectionnee, 0);

            int variation = ajouter ? quantite : -quantite;

            String erreur = controleur.modifierStock(idProduit, variation);

            if (erreur == null) {
                JOptionPane.showMessageDialog(this,
                        "Stock mis à jour avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                champVariation.setText("");
                chargerProduits();
            } else {
                JOptionPane.showMessageDialog(this,
                        erreur,
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer une quantité valide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerProduits() {
        modeleTable.setRowCount(0);
        List<Produit> produits = controleur.obtenirTousLesProduits();

        for (Produit p : produits) {
            modeleTable.addRow(new Object[] {
                    p.getIdProduit(),
                    p.getCodeProduit(),
                    p.getDescriptionProduit(),
                    p.getStock(),
                    p.getStockSecurite(),
                    p.getStockMaximum()
            });
        }
    }
}