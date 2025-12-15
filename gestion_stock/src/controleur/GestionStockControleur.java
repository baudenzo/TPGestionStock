package controleur;

import modele.Produit;
import modele.ProduitDAO;
import java.util.List;

public class GestionStockControleur {
    private ProduitDAO produitDAO;

    public GestionStockControleur() {
        this.produitDAO = new ProduitDAO();
    }

    public String creerProduit(String codeProduit, String description, String stock,
            String stockSecurite, String stockMaximum) {
        if (codeProduit == null || codeProduit.trim().isEmpty()) {
            return "Le code produit est obligatoire";
        }
        if (description == null || description.trim().isEmpty()) {
            return "La description est obligatoire";
        }

        try {
            int stockInt = Integer.parseInt(stock);
            int stockSecuriteInt = stockSecurite.isEmpty() ? 0 : Integer.parseInt(stockSecurite);
            int stockMaximumInt = Integer.parseInt(stockMaximum);

            if (stockInt < 0) {
                return "Le stock ne peut pas être négatif";
            }
            if (stockInt > stockMaximumInt) {
                return "Le stock ne peut pas dépasser le stock maximum";
            }

            Produit produit = new Produit(codeProduit, description, stockInt,
                    stockSecuriteInt, stockMaximumInt);

            if (produitDAO.creerProduit(produit)) {
                return null;
            } else {
                return "Erreur lors de la création du produit";
            }
        } catch (NumberFormatException e) {
            return "Les quantités doivent être des nombres entiers";
        }
    }

    public List<Produit> obtenirTousLesProduits() {
        return produitDAO.listerProduits();
    }

    public String modifierStock(int idProduit, int variation) {
        Produit produit = produitDAO.getProduitParId(idProduit);

        if (produit == null) {
            return "Produit introuvable";
        }

        int nouveauStock = produit.getStock() + variation;

        if (nouveauStock < 0) {
            return "Erreur : Le stock ne peut pas être négatif !";
        }

        if (nouveauStock > produit.getStockMaximum()) {
            return "Erreur : Le stock maximum (" + produit.getStockMaximum() + ") serait dépassé !";
        }

        if (produitDAO.mettreAJourStock(idProduit, nouveauStock)) {
            return null;
        } else {
            return "Erreur lors de la mise à jour du stock";
        }
    }
}