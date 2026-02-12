package com.agriflow.marketplace.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Annonce {
    private int id;
    private String titre;
    private String description;
    private TypeAnnonce type;
    private StatutAnnonce statut;
    private double prix;
    private String unitePrix;
    private String categorie;
    private String marque;
    private String modele;
    private int anneeFabrication;
    private String localisation;
    private double latitude;
    private double longitude;
    private User proprietaire;
    private List<String> photos;
    private LocalDate dateDebutDisponibilite;
    private LocalDate dateFinDisponibilite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean avecOperateur;
    private boolean assuranceIncluse;
    private double caution;
    private String conditionsLocation;
    private int quantiteDisponible;
    private String uniteQuantite;

    public Annonce() {
        this.photos = new ArrayList<>();
        this.statut = StatutAnnonce.DISPONIBLE;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        this.unitePrix = "jour";
        this.uniteQuantite = "kg";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeAnnonce getType() {
        return type;
    }

    public void setType(TypeAnnonce type) {
        this.type = type;
    }

    public StatutAnnonce getStatut() {
        return statut;
    }

    public void setStatut(StatutAnnonce statut) {
        this.statut = statut;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getUnitePrix() {
        return unitePrix;
    }

    public void setUnitePrix(String unitePrix) {
        this.unitePrix = unitePrix;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getAnneeFabrication() {
        return anneeFabrication;
    }

    public void setAnneeFabrication(int anneeFabrication) {
        this.anneeFabrication = anneeFabrication;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(User proprietaire) {
        this.proprietaire = proprietaire;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public LocalDate getDateDebutDisponibilite() {
        return dateDebutDisponibilite;
    }

    public void setDateDebutDisponibilite(LocalDate dateDebutDisponibilite) {
        this.dateDebutDisponibilite = dateDebutDisponibilite;
    }

    public LocalDate getDateFinDisponibilite() {
        return dateFinDisponibilite;
    }

    public void setDateFinDisponibilite(LocalDate dateFinDisponibilite) {
        this.dateFinDisponibilite = dateFinDisponibilite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public boolean isAvecOperateur() {
        return avecOperateur;
    }

    public void setAvecOperateur(boolean avecOperateur) {
        this.avecOperateur = avecOperateur;
    }

    public boolean isAssuranceIncluse() {
        return assuranceIncluse;
    }

    public void setAssuranceIncluse(boolean assuranceIncluse) {
        this.assuranceIncluse = assuranceIncluse;
    }

    public double getCaution() {
        return caution;
    }

    public void setCaution(double caution) {
        this.caution = caution;
    }

    public String getConditionsLocation() {
        return conditionsLocation;
    }

    public void setConditionsLocation(String conditionsLocation) {
        this.conditionsLocation = conditionsLocation;
    }

    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(int quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    public String getUniteQuantite() {
        return uniteQuantite;
    }

    public void setUniteQuantite(String uniteQuantite) {
        this.uniteQuantite = uniteQuantite;
    }

    public String getPrixFormate() {
        String unite = (unitePrix == null || unitePrix.isBlank()) ? "jour" : unitePrix;
        return String.format("%.2f DT/%s", prix, unite);
    }

    /**
     * Retourne l'URL de la première photo, ou null si aucune photo.
     */
    public String getImage() {
        if (photos != null && !photos.isEmpty()) {
            return photos.get(0);
        }
        return null;
    }

    public boolean estEnLocation() {
        return type == TypeAnnonce.LOCATION;
    }

    public boolean estEnVente() {
        return type == TypeAnnonce.VENTE;
    }

    @Override
    public String toString() {
        return "Annonce{id=" + id + ", titre='" + titre + '\'' + ", type=" + type + '}';
    }
}
