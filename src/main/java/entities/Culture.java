package entities;

import java.sql.Date;
import java.sql.Timestamp;

public class Culture {

    public enum TypeCulture {
        BLE, ORGE, MAIS, POMME_DE_TERRE, TOMATE, OLIVIER, AGRUMES, VIGNE, FRAISE, LEGUMES, AUTRE
    }

    public enum Etat {
        EN_COURS, RECOLTEE, EN_VENTE, VENDUE
    }

    private int id;
    private int parcelleId;
    private int proprietaireId;
    private String nom;
    private TypeCulture typeCulture;
    private double superficie;
    private Etat etat;
    private Date dateRecolte;
    private Double recolteEstime; // nullable
    private Timestamp dateCreation;


    public Culture() {}

    public Culture(int id, int parcelleId, int proprietaireId, String nom,
                   TypeCulture typeCulture, double superficie, Etat etat,
                   Date dateRecolte, Double recolteEstime, Timestamp dateCreation) {
        this.id = id;
        this.parcelleId = parcelleId;
        this.proprietaireId = proprietaireId;
        this.nom = nom;
        this.typeCulture = typeCulture;
        this.superficie = superficie;
        this.etat = etat;
        this.dateRecolte = dateRecolte;
        this.recolteEstime = recolteEstime;
        this.dateCreation = dateCreation;
    }

    public Culture(int parcelleId, int proprietaireId, String nom, TypeCulture typeCulture,
                   double superficie, Etat etat, Date dateRecolte, Double recolteEstime) {
        this.parcelleId = parcelleId;
        this.proprietaireId = proprietaireId;
        this.nom = nom;
        this.typeCulture = typeCulture;
        this.superficie = superficie;
        this.etat = etat;
        this.dateRecolte = dateRecolte;
        this.recolteEstime = recolteEstime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getParcelleId() { return parcelleId; }
    public void setParcelleId(int parcelleId) { this.parcelleId = parcelleId; }

    public int getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(int proprietaireId) { this.proprietaireId = proprietaireId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeCulture getTypeCulture() { return typeCulture; }
    public void setTypeCulture(TypeCulture typeCulture) { this.typeCulture = typeCulture; }

    public double getSuperficie() { return superficie; }
    public void setSuperficie(double superficie) { this.superficie = superficie; }

    public Etat getEtat() { return etat; }
    public void setEtat(Etat etat) { this.etat = etat; }

    public Date getDateRecolte() { return dateRecolte; }
    public void setDateRecolte(Date dateRecolte) { this.dateRecolte = dateRecolte; }

    public Double getRecolteEstime() { return recolteEstime; }
    public void setRecolteEstime(Double recolteEstime) { this.recolteEstime = recolteEstime; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Culture{" +
                "id=" + id +
                ", parcelleId=" + parcelleId +
                ", proprietaireId=" + proprietaireId +
                ", nom='" + nom + '\'' +
                ", typeCulture=" + typeCulture +
                ", superficie=" + superficie +
                ", etat=" + etat +
                ", dateRecolte=" + dateRecolte +
                ", recolteEstime=" + recolteEstime +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
