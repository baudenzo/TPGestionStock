package modele;

import java.time.LocalDateTime;

public class Reapprovisionnement {
    private int idReappro;
    private int idProduitExterne;
    private int quantiteCommandee;
    private LocalDateTime dateCommande;
    private LocalDateTime dateLivraisonPrevue;
    private LocalDateTime dateLivraisonReelle;
    private String statut;
    private int stockAuMomentCommande;
    private double consommationEstimee;

    public Reapprovisionnement() {
    }

    public int getIdReappro() {
        return idReappro;
    }

    public void setIdReappro(int idReappro) {
        this.idReappro = idReappro;
    }

    public int getIdProduitExterne() {
        return idProduitExterne;
    }

    public void setIdProduitExterne(int idProduitExterne) {
        this.idProduitExterne = idProduitExterne;
    }

    public int getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public void setQuantiteCommandee(int quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDateTime getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(LocalDateTime dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public LocalDateTime getDateLivraisonReelle() {
        return dateLivraisonReelle;
    }

    public void setDateLivraisonReelle(LocalDateTime dateLivraisonReelle) {
        this.dateLivraisonReelle = dateLivraisonReelle;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getStockAuMomentCommande() {
        return stockAuMomentCommande;
    }

    public void setStockAuMomentCommande(int stockAuMomentCommande) {
        this.stockAuMomentCommande = stockAuMomentCommande;
    }

    public double getConsommationEstimee() {
        return consommationEstimee;
    }

    public void setConsommationEstimee(double consommationEstimee) {
        this.consommationEstimee = consommationEstimee;
    }
}
