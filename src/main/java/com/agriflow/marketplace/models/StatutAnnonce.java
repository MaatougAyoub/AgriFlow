package com.agriflow.marketplace.models;

public enum StatutAnnonce {
    DISPONIBLE("Disponible", "#4CAF50"),
    RESERVEE("Réservée", "#FF9800"),
    LOUEE("En location", "#2196F3"),
    VENDUE("Vendue", "#9E9E9E"),
    SUSPENDUE("Suspendue", "#FF5722"),
    EXPIREE("Expirée", "#F44336");

    private final String label;
    private final String couleur;

    StatutAnnonce(String label, String couleur) {
        this.label = label;
        this.couleur = couleur;
    }

    public String getLabel() { return label; }
    public String getCouleur() { return couleur; }
    @Override public String toString() { return label; }
}
