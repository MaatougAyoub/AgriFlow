package entities;

import java.sql.Timestamp;

public class Parcelle {

    public enum TypeTerre {
        ARGILEUSE, SABLEUSE, LIMONEUSE, CALCAIRE, HUMIFERE, SALINE, MIXTE, AUTRE
    }

    private int id;
    private int agriculteurId;
    private String nom;
    private double superficie;
    private TypeTerre typeTerre;
    private String localisation;
    private Timestamp dateCreation;

    public Parcelle() {}

    public Parcelle(int id, int agriculteurId, String nom, double superficie,
                    TypeTerre typeTerre, String localisation, Timestamp dateCreation) {
        this.id = id;
        this.agriculteurId = agriculteurId;
        this.nom = nom;
        this.superficie = superficie;
        this.typeTerre = typeTerre;
        this.localisation = localisation;
        this.dateCreation = dateCreation;
    }

    public Parcelle(int agriculteurId, String nom, double superficie, TypeTerre typeTerre, String localisation) {
        this.agriculteurId = agriculteurId;
        this.nom = nom;
        this.superficie = superficie;
        this.typeTerre = typeTerre;
        this.localisation = localisation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAgriculteurId() { return agriculteurId; }
    public void setAgriculteurId(int agriculteurId) { this.agriculteurId = agriculteurId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getSuperficie() { return superficie; }
    public void setSuperficie(double superficie) { this.superficie = superficie; }

    public TypeTerre getTypeTerre() { return typeTerre; }
    public void setTypeTerre(TypeTerre typeTerre) { this.typeTerre = typeTerre; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Parcelle{" +
                "id=" + id +
                ", agriculteurId=" + agriculteurId +
                ", nom='" + nom + '\'' +
                ", superficie=" + superficie +
                ", typeTerre=" + typeTerre +
                ", localisation='" + localisation + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
