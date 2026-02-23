package entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class PlanIrrigation {
    private int planId;
    private String nomCulture;
    private LocalDateTime dateDemande;
    private String statut; // brouillon, soumis, approuvé, rejeté
    private float volumeEauPropose;
    private LocalTime tempIrrigation;   // TIME
    private LocalDateTime temp;
    private String donneesMeteojson;



    // Constructeur complet
    public PlanIrrigation(int planId,String nomCulture,
                          LocalDateTime dateDemande,String statut, float volumeEauPropose,
                          String donneesMeteojson) {
        this.planId = planId;
        this.nomCulture = nomCulture;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.volumeEauPropose = volumeEauPropose;
        this.tempIrrigation = tempIrrigation;
        this.temp = temp;
        this.donneesMeteojson = donneesMeteojson;
    }

    public PlanIrrigation(String nomCulture, LocalDateTime dateDemande,String statut, Float volumeEauPropose,LocalTime tempIrrigation,LocalDateTime temp,
                          String  donneesMeteojson) {
        this.nomCulture= nomCulture;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.volumeEauPropose = volumeEauPropose;
        this.tempIrrigation = tempIrrigation;
        this.temp = temp;
        this.donneesMeteojson = donneesMeteojson;

    }

    public PlanIrrigation(int id, String id1, LocalDate d2, String s, float f) {
    }

    public PlanIrrigation() {

    }

    public static void add(PlanIrrigation plan) {
    }


    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getNomCulture() {
        return nomCulture;
    }

    public void setNomCulture(String nomCulture) {
        this.nomCulture = nomCulture;
    }


    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public float getVolumeEauPropose() {
        return volumeEauPropose;
    }

    public void setVolumeEauPropose(float volumeEauPropose) {
        this.volumeEauPropose = volumeEauPropose;
    }

    public LocalTime getTempIrrigation() { return tempIrrigation; }
    public void setTempIrrigation(LocalTime tempIrrigation) { this.tempIrrigation = tempIrrigation; }

    public LocalDateTime getTemp() { return temp; }
    public void setTemp(LocalDateTime temp) { this.temp = temp; }

    public String getDonneesMeteojson() {
        return donneesMeteojson;
    }

    public void setDonneesMeteojson(String donneesMeteojson) {
        this.donneesMeteojson = donneesMeteojson;
    }



    @Override
    public String toString() {
        return "PlanIrrigation{" +
                "planId=" + planId +
                ", parcelleId=" + nomCulture +
                ", dateDemande=" + dateDemande +
                ", statut='" + statut + '\'' +
                ", volumeEauPropose=" + volumeEauPropose +
                '}';
    }


}