package entities;

public class Culture {
    private final int idCulture;
    private final String nomCulture;
    private final int idParcelle;
    private final float quantiteEau;

    public Culture(int idCulture, String nomCulture, int idParcelle, float quantiteEau) {
        this.idCulture = idCulture;
        this.nomCulture = nomCulture;
        this.idParcelle = idParcelle;
        this.quantiteEau = quantiteEau;
    }

    public int getIdCulture() { return idCulture; }
    public String getNomCulture() { return nomCulture; }
    public int getIdParcelle() { return idParcelle; }
    public float getQuantiteEau() { return quantiteEau; }
}