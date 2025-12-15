package service;

import modele.*;
import java.util.List;

public class SynchronisationService {
    private ProduitDAO produitDAO;
    private ProduitExterneDAO produitExterneDAO;

    public SynchronisationService() {
        this.produitDAO = new ProduitDAO();
        this.produitExterneDAO = new ProduitExterneDAO();
    }

    public boolean pushVersBaseExterne(int idEntreprise) {
        try {
            List<Produit> produitsInternes = produitDAO.listerProduits();

            for (Produit produit : produitsInternes) {
                ProduitExterne produitExterne = new ProduitExterne(
                        idEntreprise,
                        produit.getCodeProduit(),
                        produit.getDescriptionProduit(),
                        produit.getStock(),
                        produit.getStockSecurite(),
                        produit.getStockMaximum(),
                        2.0,
                        7);

                produitExterneDAO.creerOuMettreAJourProduit(produitExterne);
            }

            System.out.println("✓ PUSH réussi : " + produitsInternes.size() + " produits synchronisés");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Erreur PUSH : " + e.getMessage());
            return false;
        }
    }

    public boolean pullDepuisBaseExterne(int idEntreprise) {
        try {
            List<ProduitExterne> produitsExternes = produitExterneDAO.listerProduitsParEntreprise(idEntreprise);

            for (ProduitExterne produitExt : produitsExternes) {
                Produit produitInterne = produitDAO.getProduitParCode(produitExt.getCodeProduit());

                if (produitInterne != null) {
                    produitDAO.mettreAJourStock(produitInterne.getIdProduit(), produitExt.getStockActuel());
                }
            }

            System.out.println("✓ PULL réussi : " + produitsExternes.size() + " produits mis à jour");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Erreur PULL : " + e.getMessage());
            return false;
        }
    }

    public int compterProduitsNecessitantReappro(int idEntreprise) {
        List<ProduitExterne> produits = produitExterneDAO.listerProduitsParEntreprise(idEntreprise);
        int count = 0;

        for (ProduitExterne produit : produits) {
            if (produit.necessiteReapprovisionnement()) {
                count++;
            }
        }

        return count;
    }
}