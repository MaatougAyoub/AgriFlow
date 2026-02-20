package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private int id_utilisateur;
    private Categorie categorie;
    private String titre;
    private String description;
    private LocalDateTime dateAjout;
    private Statut statut;
    private String reponse;

    public Reclamation() {}

    public Reclamation(int id_utilisateur, Categorie categorie, String description, String titre, LocalDateTime dateAjout, Statut statut, String reponse) {
        this.id_utilisateur = id_utilisateur;
        this.categorie = categorie;
        this.description = description;
        this.titre = titre;
        this.dateAjout = dateAjout;
        this.statut = statut;
        this.reponse = reponse;
    }

    public Reclamation(int id, int id_utilisateur, Categorie categorie, String titre, String description, LocalDateTime dateAjout, Statut statut, String reponse) {
        this.id = id;
        this.id_utilisateur = id_utilisateur;
        this.categorie = categorie;
        this.titre = titre;
        this.description = description;
        this.dateAjout = dateAjout;
        this.statut = statut;
        this.reponse = reponse;
    }
    //----------------------------------------------------------------------------------------------

    public Reclamation(int id_utilisateur, Categorie categorie, String titre, String description) {
        this.id_utilisateur = id_utilisateur;
        this.categorie = categorie;
        this.titre = titre;
        this.description = description;
    }
    public Reclamation(int id, int id_utilisateur, Categorie categorie, String titre, String description) {
        this.id = id;
        this.id_utilisateur = id_utilisateur;
        this.categorie = categorie;
        this.titre = titre;
        this.description = description;
    }

    //----------------------------------------------------------------------------------------------
    //Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", id_utilisateur=" + id_utilisateur +
                ", categorie='" + categorie + '\'' +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateAjout=" + dateAjout +
                ", statut='" + statut + '\'' +
                ", reponse='" + reponse + '\'' +
                '}';
    }
}
