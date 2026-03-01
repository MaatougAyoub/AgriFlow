package services;

import entities.Diagnostic;
//import utils.MyConnection;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpertService {

    private final Connection connection;

    public ExpertService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public List<Diagnostic> getAllDiagnostics() {
        List<Diagnostic> diagnosti = new ArrayList<>();
        String sql = "SELECT id_diagnostic, id_agriculteur, nom_culture, image_path, " +
                "description, reponse_expert, date_envoi, statut FROM diagnosti";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Diagnostic diag = new Diagnostic();
                diag.setIdDiagnostic(rs.getInt("id_diagnostic"));
                diag.setIdAgriculteur(rs.getInt("id_agriculteur"));
                diag.setNomCulture(rs.getString("nom_culture"));
                diag.setImagePath(rs.getString("image_path"));
                diag.setDescription(rs.getString("description"));
                diag.setReponseExpert(rs.getString("reponse_expert"));

                Timestamp ts = rs.getTimestamp("date_envoi");
                diag.setDateEnvoi(ts != null ? ts.toLocalDateTime() : null);

                diag.setStatut(rs.getString("statut"));

                diagnosti.add(diag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" Erreur lors de la récupération des diagnostics !");
        }

        return diagnosti;
    }


    public void repondreDiagnostic(int idDiagnostic, String reponse) {
            String sql = "UPDATE diagnosti SET reponse_expert = ?, statut = 'Valide' WHERE id_diagnostic = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, reponse);
                ps.setInt(2, idDiagnostic);
                ps.executeUpdate();
                System.out.println("Diagnostic mis à jour avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(" Erreur lors de la mise à jour du diagnostic !");
            }
        }
    public void supprimerDiagnostic(int idDiagnostic) {
        String sql = "DELETE FROM diagnosti WHERE id_diagnostic = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDiagnostic);
            ps.executeUpdate();
            System.out.println("Diagnostic supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression !");
        }
    }
    public int getNombreDiagnosticsEnAttente() {
        int count = 0;
        // La requête SQL pour compter les statuts 'En attente'
        String sql = "SELECT COUNT(*) FROM diagnosti WHERE statut = 'En attente'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                count = rs.getInt(1); // Récupère le résultat du COUNT
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage des diagnostics en attente !");
            e.printStackTrace();
        }

        return count;
    }
}

