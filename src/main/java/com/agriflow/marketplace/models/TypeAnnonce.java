package com.agriflow.marketplace.models;

public enum TypeAnnonce {
    LOCATION("Location de Matériel"),
    VENTE("Vente de Produits");

    private final String label;

    TypeAnnonce(String label) { this.label = label; }
    public String getLabel() { return label; }
    @Override public String toString() { return label; }
}
