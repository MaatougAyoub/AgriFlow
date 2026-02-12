package com.agriflow.marketplace.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private Annonce annonce;
    private User demandeur;
    private User proprietaire;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int quantite;
    private double prixTotal;
    private double caution;
    private StatutReservation statut;
    private String messageDemande;
    private String reponseProprietaire;
    private LocalDateTime dateDemande;
    private LocalDateTime dateReponse;
    private String contratUrl;
    private boolean contratSigne;
    private LocalDateTime dateSignatureContrat;

    public Reservation() {
        this.statut = StatutReservation.EN_ATTENTE;
        this.dateDemande = LocalDateTime.now();
        this.contratSigne = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Annonce getAnnonce() { return annonce; }
    public void setAnnonce(Annonce annonce) { this.annonce = annonce; }

    public User getDemandeur() { return demandeur; }
    public void setDemandeur(User demandeur) { this.demandeur = demandeur; }

    public User getProprietaire() { return proprietaire; }
    public void setProprietaire(User proprietaire) { this.proprietaire = proprietaire; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public double getCaution() { return caution; }
    public void setCaution(double caution) { this.caution = caution; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public String getContratUrl() { return contratUrl; }
    public void setContratUrl(String contratUrl) { this.contratUrl = contratUrl; }

    public boolean isContratSigne() { return contratSigne; }
    public void setContratSigne(boolean contratSigne) { this.contratSigne = contratSigne; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public String getMessageDemande() { return messageDemande; }
    public void setMessageDemande(String messageDemande) { this.messageDemande = messageDemande; }

    public String getReponseProprietaire() { return reponseProprietaire; }
    public void setReponseProprietaire(String reponseProprietaire) { this.reponseProprietaire = reponseProprietaire; }

    public LocalDateTime getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDateTime dateDemande) { this.dateDemande = dateDemande; }

    public LocalDateTime getDateReponse() { return dateReponse; }
    public void setDateReponse(LocalDateTime dateReponse) { this.dateReponse = dateReponse; }

    public LocalDateTime getDateSignatureContrat() { return dateSignatureContrat; }
    public void setDateSignatureContrat(LocalDateTime dateSignatureContrat) { this.dateSignatureContrat = dateSignatureContrat; }

    public int getNombreJours() {
        if (dateDebut == null || dateFin == null) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }

    public void calculerPrixTotal() {
        if (annonce != null && annonce.estEnLocation()) {
            this.prixTotal = annonce.getPrix() * getNombreJours();
        } else if (annonce != null) {
            this.prixTotal = annonce.getPrix() * quantite;
        }
    }

    public boolean peutEtreSigne() {
        return statut == StatutReservation.ACCEPTEE || statut == StatutReservation.EN_COURS;
    }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", statut=" + statut + ", prixTotal=" + prixTotal + '}';
    }
}
