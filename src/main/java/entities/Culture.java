package entities;

public class Culture {
    private int id;
    private String nom;
    private int parcelle_id;
    private float superficie; // Récupérée depuis la table Parcelle
    private TypeCulture typeCulture; // Pour déterminer le facteur multiplicateur

    public enum TypeCulture {
        BLE, ORGE, MAIS, POMME_DE_TERRE, TOMATE, OLIVIER,
        AGRUMES, VIGNE, FRAISE, LEGUMES, AUTRE
    }

    public Culture(int id, String nom, int parcelle_id, float superficie, TypeCulture typeCulture) {
        this.id = id;
        this.nom = nom;
        this.parcelle_id = parcelle_id;
        this.superficie = superficie;
        this.typeCulture = typeCulture;
    }



    public float calculerBesoinEau() {
        if (typeCulture == null) return 0;

        float f = switch (typeCulture) {
            case BLE, ORGE, FRAISE, AUTRE -> 2.0f;
            case MAIS, TOMATE -> 4.0f;
            case POMME_DE_TERRE, AGRUMES, LEGUMES -> 3.0f;
            case OLIVIER, VIGNE -> 1.0f;
        };

        return f * superficie;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getIdParcelle() { return parcelle_id; }
    public void setIdParcelle(int parcelle_id) { this.parcelle_id = parcelle_id; }

    public float getSuperficie() { return superficie; }
    public void setSuperficie(float superficie) {
        this.superficie = superficie;
    }

    public TypeCulture getTypeCulture() { return typeCulture; }
    public void setTypeCulture(TypeCulture typeCulture) {
        this.typeCulture = typeCulture;
    }


}