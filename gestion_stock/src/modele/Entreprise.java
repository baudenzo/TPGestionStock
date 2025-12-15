package modele;

import java.time.LocalDateTime;

public class Entreprise {
    private int idEntreprise;
    private String nomEntreprise;
    private String codeEntreprise;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDateTime dateCreation;

    public Entreprise() {
    }

    public Entreprise(int idEntreprise, String nomEntreprise, String codeEntreprise) {
        this.idEntreprise = idEntreprise;
        this.nomEntreprise = nomEntreprise;
        this.codeEntreprise = codeEntreprise;
    }

    public int getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(int idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getCodeEntreprise() {
        return codeEntreprise;
    }

    public void setCodeEntreprise(String codeEntreprise) {
        this.codeEntreprise = codeEntreprise;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}