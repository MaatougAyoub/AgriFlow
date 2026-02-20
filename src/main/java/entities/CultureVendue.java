package entities;

import java.sql.Date;
import java.sql.Timestamp;

public class CultureVendue {

    private int idVente;
    private int idCulture;
    private Integer idAcheteur; // peut être null si pas encore vendue
    private Date dateVente;     // peut être null
    private Timestamp datePublication;
    private double prixVente;

    public CultureVendue() {}

    public CultureVendue(int idVente, int idCulture, Integer idAcheteur,
                         Date dateVente, Timestamp datePublication, double prixVente) {
        this.idVente = idVente;
        this.idCulture = idCulture;
        this.idAcheteur = idAcheteur;
        this.dateVente = dateVente;
        this.datePublication = datePublication;
        this.prixVente = prixVente;
    }

    public CultureVendue(int idCulture, double prixVente) {
        this.idCulture = idCulture;
        this.prixVente = prixVente;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public int getIdCulture() {
        return idCulture;
    }

    public void setIdCulture(int idCulture) {
        this.idCulture = idCulture;
    }

    public Integer getIdAcheteur() {
        return idAcheteur;
    }

    public void setIdAcheteur(Integer idAcheteur) {
        this.idAcheteur = idAcheteur;
    }

    public Date getDateVente() {
        return dateVente;
    }

    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }

    public Timestamp getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Timestamp datePublication) {
        this.datePublication = datePublication;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    @Override
    public String toString() {
        return "CultureVendue{" +
                "idVente=" + idVente +
                ", idCulture=" + idCulture +
                ", idAcheteur=" + idAcheteur +
                ", dateVente=" + dateVente +
                ", datePublication=" + datePublication +
                ", prixVente=" + prixVente +
                '}';
    }
}
