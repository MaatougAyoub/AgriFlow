package entities;

public class Parcelle {
    private int idParcelle;
    private String nom;
    private String localisation; // Format "lat,lon"

    public Parcelle(int idParcelle, String nom, String localisation) {
        this.idParcelle = idParcelle;
        this.nom = nom;
        this.localisation = localisation;
    }

    // Getters & Setters
    public int getIdParcelle() { return idParcelle; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public String getNom() { return nom; }
}