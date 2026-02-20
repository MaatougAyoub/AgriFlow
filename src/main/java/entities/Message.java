package entities;

import java.time.LocalDateTime;

/**
 * Modèle Message pour la messagerie P2P entre agriculteurs.
 * Permet la communication directe entre deux utilisateurs.
 */
public class Message {
    private int id;
    private User expediteur;
    private User destinataire;
    private String sujet;
    private String contenu;
    private Annonce annonce;           // Message lié à une annonce (optionnel)
    private Reservation reservation;    // Message lié à une réservation (optionnel)
    private boolean lu;
    private LocalDateTime dateLecture;
    private LocalDateTime dateEnvoi;

    public Message() {
        this.lu = false;
        this.dateEnvoi = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getExpediteur() { return expediteur; }
    public void setExpediteur(User expediteur) { this.expediteur = expediteur; }

    public User getDestinataire() { return destinataire; }
    public void setDestinataire(User destinataire) { this.destinataire = destinataire; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Annonce getAnnonce() { return annonce; }
    public void setAnnonce(Annonce annonce) { this.annonce = annonce; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public LocalDateTime getDateLecture() { return dateLecture; }
    public void setDateLecture(LocalDateTime dateLecture) { this.dateLecture = dateLecture; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    /**
     * Marque le message comme lu et enregistre la date de lecture.
     */
    public void marquerCommeLu() {
        this.lu = true;
        this.dateLecture = LocalDateTime.now();
    }

    /**
     * Vérifie si le message est lié à une annonce.
     */
    public boolean estLieAUneAnnonce() {
        return annonce != null;
    }

    /**
     * Vérifie si le message est lié à une réservation.
     */
    public boolean estLieAUneReservation() {
        return reservation != null;
    }

    /**
     * Retourne un aperçu du contenu (100 premiers caractères).
     */
    public String getApercu() {
        if (contenu == null || contenu.isEmpty()) return "";
        return contenu.length() > 100 ? contenu.substring(0, 100) + "..." : contenu;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", de=" + (expediteur != null ? expediteur.getNomComplet() : "?") +
                ", à=" + (destinataire != null ? destinataire.getNomComplet() : "?") +
                ", sujet='" + sujet + '\'' +
                ", lu=" + lu +
                '}';
    }
}