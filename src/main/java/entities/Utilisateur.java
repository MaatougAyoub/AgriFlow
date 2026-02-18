package entities;

import java.time.LocalDate;

public class Utilisateur {
    protected int id;
    protected String nom;
    protected String prenom;
    protected int cin;
    protected String email;
    protected String motDePasse;
    protected String role;
    protected LocalDate dateCreation;
    protected String signature;

    public Utilisateur() { /*Constructeur par défaut*/ }

    //Constructeur pour les updates (sans id)
    public Utilisateur(String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = dateCreation;
        this.signature = signature;
    }

    //constructeur avec id
    public Utilisateur(int id, String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = dateCreation;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", cin=" + cin +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", role='" + role + '\'' +
                ", dateCreation=" + dateCreation +
                ", signature='" + signature + '\'' +
                '}';
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
