package entities;

import java.time.LocalDate;

// Entity User - tmathel table "utilisateurs" fl base (mta3 Ayoub)
// hedhi partajee m3a module gestion utilisateurs
public class User {
    private int id;
    private String nom;
    private String prenom;
    private int cin;
    private String email;
    private String motDePasse;
    private String role;           // ADMIN, AGRICULTEUR, EXPERT
    private LocalDate dateCreation;
    private String signature;      // chemin vers image signature

    // Champs supplémentaires utilisés par le Marketplace (pas dans utilisateurs mais utiles)
    private String telephone;
    private String region;

    public User() {
        this.dateCreation = LocalDate.now();
    }

    public User(int id, String nom, String prenom, String email) {
        this();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    // — Getters / Setters —

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public int getCin() { return cin; }
    public void setCin(int cin) { this.cin = cin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', prenom='" + prenom + "', role='" + role + "'}";
    }
}