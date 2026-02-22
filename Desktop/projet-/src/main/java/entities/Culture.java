package entities;

public class Culture {
    private int idCulture;
    private String nomCulture;
    private int idParcelle;
    private float quantiteEau; // Ajouté pour le calcul IA

    public Culture(int idCulture, String nomCulture, int idParcelle, float quantiteEau) {
        this.idCulture = idCulture;
        this.nomCulture = nomCulture;
        this.idParcelle = idParcelle;
        this.quantiteEau = quantiteEau;
    }

    public int getIdCulture() { return idCulture; }
    public int getIdParcelle() { return idParcelle; }
    public String getNomCulture() { return nomCulture; }
    public float getQuantiteEau() { return quantiteEau; }
}