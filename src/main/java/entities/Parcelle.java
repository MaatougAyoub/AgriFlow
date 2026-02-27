package entities;

public class Parcelle {
    private int id;
    private String nom;
    private String localisation; // Format "lat,lon"

    public Parcelle(int id, String nom, String localisation) {
        this.id = id;
        this.nom = nom;
        this.localisation = localisation;
    }

    // Getters & Setters
    public int getIdParcelle() { return id; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public String getNom() { return nom; }
}