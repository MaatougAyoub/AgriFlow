package services;

import entities.Diagnostic;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticService {

    private final Connection connection;

    public DiagnosticService() {
        connection = MyDatabase.getInstance().getConnection();
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
            ps.setString(5, d.getReponseExpert());
            ps.setTimestamp(6, d.getDateEnvoi() != null ? Timestamp.valueOf(d.getDateEnvoi()) : null);
            ps.setString(7, d.getStatut());

            ps.executeUpdate();
            System.out.println("✅ Diagnostic ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'ajout du diagnostic !");
        }
    }

    public List<Diagnostic> recupererParAgriculteur(int idAgri) {
        List<Diagnostic> list = new ArrayList<>();

        System.out.println("══════════════════════════════════════════");
        System.out.println("🔍 recupererParAgriculteur() appelé avec idAgri = " + idAgri);

        // ⚠️ ÉTAPE 1 : Vérifier la connexion
        if (connection == null) {
            System.err.println("❌ CONNEXION NULL ! La base de données n'est pas connectée !");
            return list;
        }

        try {
            if (connection.isClosed()) {
                System.err.println("❌ CONNEXION FERMÉE !");
                return list;
            }
            System.out.println("✅ Connexion BDD OK");
        } catch (SQLException e) {
            System.err.println("❌ Erreur vérification connexion : " + e.getMessage());
        }

        // ⚠️ ÉTAPE 2 : D'abord compter TOUT ce qui existe dans la table
        try (Statement stmt = connection.createStatement();
             ResultSet rsCount = stmt.executeQuery("SELECT COUNT(*) AS total FROM diagnosti")) {
            if (rsCount.next()) {
                System.out.println("📊 Nombre total de lignes dans diagnosti : " + rsCount.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur comptage : " + e.getMessage());
        }

        // ⚠️ ÉTAPE 3 : Afficher TOUS les id_agriculteur existants
        try (Statement stmt = connection.createStatement();
             ResultSet rsAll = stmt.executeQuery("SELECT id_diagnostic, id_agriculteur, nom_culture, statut FROM diagnosti")) {
            System.out.println("📋 Contenu complet de la table diagnosti :");
            while (rsAll.next()) {
                System.out.println("   → id_diagnostic=" + rsAll.getInt("id_diagnostic") +
                        " | id_agriculteur=" + rsAll.getInt("id_agriculteur") +
                        " | culture=" + rsAll.getString("nom_culture") +
                        " | statut=" + rsAll.getString("statut"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture table : " + e.getMessage());
        }

        // ⚠️ ÉTAPE 4 : La vraie requête filtrée
        String sql = "SELECT id_diagnostic, id_agriculteur, nom_culture, image_path, " +
                "description, reponse_expert, statut, date_envoi " +
                "FROM diagnosti WHERE id_agriculteur = ? ORDER BY date_envoi DESC";

        System.out.println("🔎 Exécution requête avec id_agriculteur = " + idAgri);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAgri);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Diagnostic d = new Diagnostic();
                d.setIdDiagnostic(rs.getInt("id_diagnostic"));
                d.setIdAgriculteur(rs.getInt("id_agriculteur"));
                d.setNomCulture(rs.getString("nom_culture"));
                d.setImagePath(rs.getString("image_path"));
                d.setDescription(rs.getString("description"));
                d.setReponseExpert(rs.getString("reponse_expert"));
                d.setStatut(rs.getString("statut"));

                Timestamp ts = rs.getTimestamp("date_envoi");
                d.setDateEnvoi(ts != null ? ts.toLocalDateTime() : null);

                list.add(d);
                System.out.println("✅ TROUVÉ : id=" + d.getIdDiagnostic() +
                        " | culture=" + d.getNomCulture() +
                        " | statut=" + d.getStatut());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL requête filtrée : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("📊 RÉSULTAT FINAL : " + list.size() + " diagnostics pour id_agriculteur=" + idAgri);
        System.out.println("══════════════════════════════════════════");

        return list;
    }
}