package services;

import entities.Diagnostic;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticService {

    private final Connection connection;

    public DiagnosticService() {
        connection = MyConnection.getInstance().getConnection();
    }

    public void ajouterDiagnostic(Diagnostic d) {
        String sql = "INSERT INTO diagnosti (id_agriculteur, nom_culture, image_path, " +
                "description, reponse_expert, date_envoi, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, d.getIdAgriculteur());
            ps.setString(2, d.getNomCulture());
            ps.setString(3, d.getImagePath());
            ps.setString(4, d.getDescription());
            ps.setString(5, d.getReponseExpert()); // peut être null au départ
            ps.setTimestamp(6, d.getDateEnvoi() != null ? Timestamp.valueOf(d.getDateEnvoi()) : null);
            ps.setString(7, d.getStatut());

            ps.executeUpdate();
            System.out.println("Diagnostic ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du diagnostic !");
        }
    }

    public List<Diagnostic> recupererParAgriculteur(int idAgri) {
        List<Diagnostic> list = new ArrayList<>();
        // On récupère le nom de la culture, le chemin de l'image et le statut
        String sql = "SELECT nom_culture, image_path, statut, date_envoi FROM diagnosti WHERE id_agriculteur = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idAgri);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Diagnostic d = new Diagnostic();
                    d.setNomCulture(rs.getString("nom_culture"));
                    d.setImagePath(rs.getString("image_path"));
                    d.setStatut(rs.getString("statut"));
                    Timestamp ts = rs.getTimestamp("date_envoi");
                    d.setDateEnvoi(ts != null ? ts.toLocalDateTime() : null);
                    list.add(d);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }
}
