package entities;

import java.time.LocalDate;

public class Agriculteur extends Utilisateur{
    private String carte_pro;
    private String adresse;
    private String parcelles; //à changer le type par List<Parcelle>

    public Agriculteur() {}

    // constructeur sans id
    public Agriculteur(String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, String carte_pro, String adresse, String parcelles) {
        super(nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.carte_pro = carte_pro;
        this.adresse = adresse;
        this.parcelles = parcelles;
    }

    // constructeur avec id
    public Agriculteur(int id, String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, String carte_pro, String adresse, String parcelles) {
        super(id, nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.carte_pro = carte_pro;
        this.adresse = adresse;
        this.parcelles = parcelles;
    }

    public String getCarte_pro() {
        return carte_pro;
    }

    public void setCarte_pro(String carte_pro) {
        this.carte_pro = carte_pro;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getParcelles() {
        return parcelles;
    }

    public void setParcelles(String parcelles) {
        this.parcelles = parcelles;
    }

    @Override
    public String toString() {
        return "Agriculteur{" +
                "carte_pro='" + carte_pro + '\'' +
                ", adresse='" + adresse + '\'' +
                ", parcelles='" + parcelles + '\'' +
                ", " + super.toString();
    }
}
