package com.agriflow.marketplace.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String region;
    private String photoProfil;
    private byte[] signatureImage;
    private LocalDateTime dateInscription;
    private boolean actif;

    public User() {
        this.dateInscription = LocalDateTime.now();
        this.actif = true;
    }

    public User(int id, String nom, String prenom, String email, String telephone) {
        this();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public byte[] getSignatureImage() { return signatureImage; }
    public void setSignatureImage(byte[] signatureImage) { this.signatureImage = signatureImage; }

    public boolean hasSignature() {
        return signatureImage != null && signatureImage.length > 0;
    }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + '\'' + ", prenom='" + prenom + '\'' + '}';
    }
}
