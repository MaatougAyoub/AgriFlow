package entities;

import java.time.LocalDateTime;

public class Diagnostic {

    private int idDiagnostic;
    private int idAgriculteur;
    private String nomCulture;
    private String imagePath;
    private String description;
    private String reponseExpert;
    private String statut;
    private LocalDateTime dateEnvoi;




    public Diagnostic() {
        this.idDiagnostic = idDiagnostic;
        this.idAgriculteur = idAgriculteur;
        this.nomCulture = nomCulture;
        this.imagePath = imagePath;
        this.description = description;
    }

    // --- GETTERS & SETTERS ---
    public int getIdDiagnostic() { return idDiagnostic; }
    public void setIdDiagnostic(int idDiagnostic) { this.idDiagnostic = idDiagnostic; }

    public int getIdAgriculteur() { return idAgriculteur; }
    public void setIdAgriculteur(int idAgriculteur) { this.idAgriculteur = idAgriculteur; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReponseExpert() { return reponseExpert; }
    public void setReponseExpert(String reponseExpert) { this.reponseExpert = reponseExpert; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public String getNomCulture() { return nomCulture; }
    public void setNomCulture(String nomCulture) { this.nomCulture = nomCulture; }


}
