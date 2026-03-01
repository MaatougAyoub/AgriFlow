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
    // ====== Champs issus de culture_vendue (fusionnés dans Culture) ======
    private Integer idAcheteur;        // nullable (pas encore vendu)
    private Date dateVente;            // nullable
    private Date datePublication;      // nullable (pas encore publié en vente)
    private Double prixVente;          // nullable (pas encore en vente)

    public Culture() {}

    // Constructeur complet (avec champs vente)
    //intégration badis---------------------------------------
    public Culture(int id, int parcelleId, int proprietaireId, String nom,
                   TypeCulture typeCulture, double superficie, Etat etat,
                   Date dateRecolte, Double recolteEstime, Timestamp dateCreation,
                   Integer idAcheteur, Date dateVente, Date datePublication, Double prixVente) {
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
        this.idAcheteur = idAcheteur;
        this.dateVente = dateVente;
        this.datePublication = datePublication;
        this.prixVente = prixVente;
    }


    // Constructeur (création) sans id/dateCreation (et champs vente optionnels)
    //intégration badis---------------------------------------
    public Culture(int parcelleId, int proprietaireId, String nom, TypeCulture typeCulture,
                   double superficie, Etat etat, Date dateRecolte, Double recolteEstime,
                   Integer idAcheteur, Date dateVente, Date datePublication, Double prixVente) {
        this.parcelleId = parcelleId;
        this.proprietaireId = proprietaireId;
        this.nom = nom;
        this.typeCulture = typeCulture;
        this.superficie = superficie;
        this.etat = etat;
        this.dateRecolte = dateRecolte;
        this.recolteEstime = recolteEstime;

        this.idAcheteur = idAcheteur;
        this.dateVente = dateVente;
        this.datePublication = datePublication;
        this.prixVente = prixVente;
    }






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

    public Culture(int id, String nom, int parcelleId, double superficie) {
        this.id = id;
        this.parcelleId = parcelleId;
        this.nom = nom;
        this.superficie = superficie;

    }

    public Culture(int id, String nom, int parcelleId, double superficie, TypeCulture typeCulture) {
        this.id = id;
        this.nom = nom;
        this.parcelleId = parcelleId;
        this.superficie = superficie;
        this.typeCulture = typeCulture;
    }

    public float calculerBesoinEau() {
        if (typeCulture == null)
            return 0;

        float f = switch (typeCulture) {
            case BLE, ORGE, FRAISE, AUTRE -> 2.0f;
            case MAIS, TOMATE -> 4.0f;
            case POMME_DE_TERRE, AGRUMES, LEGUMES -> 3.0f;
            case OLIVIER, VIGNE -> 1.0f;
        };

        return (float) (f * superficie);
    }

    // ====== Getters/Setters existants ======
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

    // ====== Getters/Setters nouveaux (vente) ======
    public Integer getIdAcheteur() { return idAcheteur; }
    public void setIdAcheteur(Integer idAcheteur) { this.idAcheteur = idAcheteur; }

    public Date getDateVente() { return dateVente; }
    public void setDateVente(Date dateVente) { this.dateVente = dateVente; }

    public Date getDatePublication() { return datePublication; }
    public void setDatePublication(Date datePublication) { this.datePublication = datePublication; }

    public Double getPrixVente() { return prixVente; }
    public void setPrixVente(Double prixVente) { this.prixVente = prixVente; }

    public float getQuantiteEau() {
        if (typeCulture == null)
            return 0;

        float f = switch (typeCulture) {
            case BLE -> 2;
            case ORGE -> 2;
            case MAIS -> 4;
            case POMME_DE_TERRE -> 3;
            case TOMATE -> 4;
            case OLIVIER -> 1;
            case AGRUMES -> 3;
            case VIGNE -> 1;
            case FRAISE -> 2;
            case LEGUMES -> 3;
            case AUTRE -> 2;
        };
        return (float) (f * superficie);
    }

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
                ", idAcheteur=" + idAcheteur +
                ", dateVente=" + dateVente +
                ", datePublication=" + datePublication +
                ", prixVente=" + prixVente +
                '}';
    }
}