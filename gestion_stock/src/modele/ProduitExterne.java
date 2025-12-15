package modele;

import java.time.LocalDateTime;

public class ProduitExterne {
    private int idProduit;
    private int idEntreprise;
    private String codeProduit;
    private String descriptionProduit;
    private int stockActuel;
    private int stockSecurite;
    private int stockMaximum;
    private double consommationJournaliere;
    private int delaiLivraisonJours;
    private LocalDateTime dateDerniereSync;
    private LocalDateTime dateMiseAJour;

    public ProduitExterne() {
    }

    public ProduitExterne(int idEntreprise, String codeProduit, String descriptionProduit,
            int stockActuel, int stockSecurite, int stockMaximum,
            double consommationJournaliere, int delaiLivraisonJours) {
        this.idEntreprise = idEntreprise;
        this.codeProduit = codeProduit;
        this.descriptionProduit = descriptionProduit;
        this.stockActuel = stockActuel;
        this.stockSecurite = stockSecurite;
        this.stockMaximum = stockMaximum;
        this.consommationJournaliere = consommationJournaliere;
        this.delaiLivraisonJours = delaiLivraisonJours;
    }

    public boolean necessiteReapprovisionnement() {
        double stockProjecte = stockActuel - (consommationJournaliere * delaiLivraisonJours);
        return stockProjecte <= stockSecurite;
    }

    public int calculerQuantiteReappro() {
        double stockProjecte = stockActuel - (consommationJournaliere * delaiLivraisonJours);
        int quantiteNecessaire = (int) Math.ceil(stockMaximum - stockProjecte);
        return Math.max(0, Math.min(quantiteNecessaire, stockMaximum - stockActuel));
    }

    public double getPourcentageStock() {
        return (double) stockActuel / stockMaximum * 100;
    }

    public String getNiveauAlerte() {
        if (stockActuel <= stockSecurite) {
            return "CRITIQUE";
        } else if (stockActuel <= stockSecurite + 10) {
            return "ATTENTION";
        } else {
            return "NORMAL";
        }
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public int getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(int idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getDescriptionProduit() {
        return descriptionProduit;
    }

    public void setDescriptionProduit(String descriptionProduit) {
        this.descriptionProduit = descriptionProduit;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public int getStockSecurite() {
        return stockSecurite;
    }

    public void setStockSecurite(int stockSecurite) {
        this.stockSecurite = stockSecurite;
    }

    public int getStockMaximum() {
        return stockMaximum;
    }

    public void setStockMaximum(int stockMaximum) {
        this.stockMaximum = stockMaximum;
    }

    public double getConsommationJournaliere() {
        return consommationJournaliere;
    }

    public void setConsommationJournaliere(double consommationJournaliere) {
        this.consommationJournaliere = consommationJournaliere;
    }

    public int getDelaiLivraisonJours() {
        return delaiLivraisonJours;
    }

    public void setDelaiLivraisonJours(int delaiLivraisonJours) {
        this.delaiLivraisonJours = delaiLivraisonJours;
    }

    public LocalDateTime getDateDerniereSync() {
        return dateDerniereSync;
    }

    public void setDateDerniereSync(LocalDateTime dateDerniereSync) {
        this.dateDerniereSync = dateDerniereSync;
    }

    public LocalDateTime getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(LocalDateTime dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }
}