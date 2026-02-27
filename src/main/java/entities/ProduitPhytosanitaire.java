package entities;

public class ProduitPhytosanitaire {
    private int idProduit;
    private String nomProduit;
    private String dosage;
    private String frequenceApplication;
    private String remarques;

    public ProduitPhytosanitaire() {}

    public ProduitPhytosanitaire(String nomProduit, String dosage, String frequenceApplication, String remarques) {
        this.nomProduit = nomProduit;
        this.dosage = dosage;
        this.frequenceApplication = frequenceApplication;
        this.remarques = remarques;
    }

    // Getters et Setters
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }
    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequenceApplication() { return frequenceApplication; }
    public void setFrequenceApplication(String frequenceApplication) { this.frequenceApplication = frequenceApplication; }
    public String getRemarques() { return remarques; }
    public void setRemarques(String remarques) { this.remarques = remarques; }

    @Override
    public String toString() { return nomProduit; }
}