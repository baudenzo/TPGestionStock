package modele;

import java.time.LocalDateTime;

public class Produit {
    private int idProduit;
    private String codeProduit;
    private String descriptionProduit;
    private int stock;
    private int stockSecurite;
    private int stockMaximum;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;

    public Produit() {
    }

    public Produit(String codeProduit, String descriptionProduit, int stock,
            int stockSecurite, int stockMaximum) {
        this.codeProduit = codeProduit;
        this.descriptionProduit = descriptionProduit;
        this.stock = stock;
        this.stockSecurite = stockSecurite;
        this.stockMaximum = stockMaximum;
    }

    public boolean peutAugmenterStock(int quantite) {
        return (stock + quantite) <= stockMaximum;
    }

    public boolean peutDiminuerStock(int quantite) {
        return (stock - quantite) >= 0;
    }

    public boolean estEnAlerte() {
        return stock < stockSecurite;
    }

    public boolean estProcheAlerte() {
        return stock < (stockSecurite + 10) && stock >= stockSecurite;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(LocalDateTime dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }
}