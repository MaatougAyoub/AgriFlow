package entities;

import java.time.LocalDate;

public class Expert extends Utilisateur {
    private String certification;

    //Constructeur par defaut
    public Expert() {}

    // constructeur sans id
    public Expert(String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, String certification) {
        super(nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.certification = certification;
    }

    // constructeur avec id
    public Expert(int id, String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, String certification) {
        super(id, nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.certification = certification;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    @Override
    public String toString() {
        return "Expert{" +
                "certification='" + certification + '\'' +
                ", " + super.toString();
    }
}

