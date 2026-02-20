package entities;

public enum StatutReservation {
    EN_ATTENTE("En attente", "#FF9800"),
    ACCEPTEE("Acceptée", "#4CAF50"),
    REFUSEE("Refusée", "#F44336"),
    EN_COURS("En cours", "#2196F3"),
    TERMINEE("Terminée", "#9E9E9E"),
    ANNULEE("Annulée", "#757575");

    private final String label;
    private final String couleur;

    StatutReservation(String label, String couleur) {
        this.label = label;
        this.couleur = couleur;
    }

    public String getLabel() { return label; }
    public String getCouleur() { return couleur; }
    @Override public String toString() { return label; }
}