package entities;

/**
 * Modèle PhotoAnnonce pour gérer les photos 3D des annonces.
 * Permet d'ajouter plusieurs photos à une annonce avec un ordre d'affichage.
 */
public class PhotoAnnonce {
    private int id;
    private int annonceId;
    private String urlPhoto;
    private int ordre;

    public PhotoAnnonce() {
        this.ordre = 0;
    }

    public PhotoAnnonce(int annonceId, String urlPhoto, int ordre) {
        this.annonceId = annonceId;
        this.urlPhoto = urlPhoto;
        this.ordre = ordre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAnnonceId() { return annonceId; }
    public void setAnnonceId(int annonceId) { this.annonceId = annonceId; }

    public String getUrlPhoto() { return urlPhoto; }
    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }

    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }

    /**
     * Vérifie si la photo est la photo principale (ordre = 0).
     */
    public boolean estPhotoPrincipale() {
        return ordre == 0;
    }

    @Override
    public String toString() {
        return "PhotoAnnonce{" +
                "id=" + id +
                ", annonceId=" + annonceId +
                ", ordre=" + ordre +
                ", principal=" + estPhotoPrincipale() +
                '}';
    }
}
