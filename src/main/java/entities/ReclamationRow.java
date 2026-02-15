package entities;

import java.time.LocalDateTime;

public class ReclamationRow {
    private final int id;
    private final int utilisateurId;

    private final String nom;
    private final String prenom;
    private final String role;
    private final String email;

    private final String categorie;
    private final String titre;
    private final String description;
    private final LocalDateTime dateCreation;
    private final String statut;
    private final String reponse;

    public ReclamationRow(int id, int utilisateurId, String nom, String prenom, String role, String email,
                          String categorie, String titre, String description, LocalDateTime dateCreation,
                          String statut, String reponse) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.email = email;
        this.categorie = categorie;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.reponse = reponse;
    }

    public int getId() { return id; }
    public int getUtilisateurId() { return utilisateurId; }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getRole() { return role; }
    public String getEmail() { return email; }

    public String getCategorie() { return categorie; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public String getStatut() { return statut; }
    public String getReponse() { return reponse; }
}