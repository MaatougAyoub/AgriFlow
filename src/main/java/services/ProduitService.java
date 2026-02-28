package services;

import entities.ProduitPhytosanitaire;
import utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProduitService {
    private final Connection conn;

    public ProduitService() {
        conn = MyConnection.getInstance().getConnection();
    }

    public List<ProduitPhytosanitaire> getAll() {
        List<ProduitPhytosanitaire> list = new ArrayList<>();
        String sql = "SELECT * FROM produits_phytosanitaires ORDER BY nom_produit ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProduitPhytosanitaire(
                        rs.getString("nom_produit"),
                        rs.getString("dosage"),
                        rs.getString("frequence_application"),
                        rs.getString("remarques")
                ));
                // On récupère l'ID pour les futures modifs
                list.get(list.size()-1).setIdProduit(rs.getInt("id_produit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void ajouterProduit(ProduitPhytosanitaire p) throws SQLException {
        String sql = "INSERT INTO produits_phytosanitaires (nom_produit, dosage, frequence_application, remarques) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomProduit());
            ps.setString(2, p.getDosage());
            ps.setString(3, p.getFrequenceApplication());
            ps.setString(4, p.getRemarques());
            ps.executeUpdate();
        }
    }
    public List<ProduitPhytosanitaire> afficherProduits() {

        List<ProduitPhytosanitaire> list = new ArrayList<>();

        String sql = "SELECT * FROM produits_phytosanitaires";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                ProduitPhytosanitaire p = new ProduitPhytosanitaire();

                p.setIdProduit(rs.getInt("id_produit"));

                p.setNomProduit(rs.getString("nom_produit"));

                p.setDosage(rs.getString("dosage"));

                p.setFrequenceApplication(rs.getString("frequence_application"));

                p.setRemarques(rs.getString("remarques"));

                list.add(p);

            }

        } catch (SQLException e) {

            System.err.println("Erreur affichage : " + e.getMessage());

        }

        return list;

    }
    public boolean supprimerProduit(int id) {
        // J'ai corrigé le nom de la table et le nom de la colonne de l'identifiant
        String sql = "DELETE FROM produits_phytosanitaires WHERE id_produit = ?";

        // Un DELETE ne nécessite qu'un PreparedStatement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            // executeUpdate() renvoie le nombre de lignes modifiées/supprimées.
            // Si c'est > 0, c'est que la suppression a réussi.
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit : " + e.getMessage());
            return false;
        }
    }

}