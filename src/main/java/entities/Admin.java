package entities;

import java.time.LocalDate;

public class Admin extends Utilisateur {
    private Double revenus;

    //Constructeur par defaut
    public Admin() {}

    public Admin(String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, Double revenus) {
        super(nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.revenus = revenus;
    }

    public Admin(int id, String nom, String prenom, int cin, String email, String motDePasse, String role, LocalDate dateCreation, String signature, Double revenus) {
        super(id, nom, prenom, cin, email, motDePasse, role, dateCreation, signature);
        this.revenus = revenus;
    }

    //Gettters and Setters

    public Double getRevenus() {
        return revenus;
    }

    public void setRevenus(Double revenus) {
        this.revenus = revenus;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "revenus=" + revenus +
                ", " + super.toString();
    }
}
