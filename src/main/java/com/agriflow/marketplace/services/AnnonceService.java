package com.agriflow.marketplace.services;

import com.agriflow.marketplace.models.*;
import com.agriflow.marketplace.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour gérer les annonces du Marketplace.
 * Implémente l'interface IService avec des PreparedStatement JDBC.
 */
public class AnnonceService implements IService<Annonce> {

    private Connection cnx;
    private final UserService userService;

    /**
     * Constructeur : Récupère la connexion via le Singleton MyDatabase.
     */
    public AnnonceService() {
        this.cnx = MyDatabase.getInstance().getCnx();
        this.userService = new UserService();
    }

    /**
     * Ajoute une nouvelle annonce dans la base de données.
     *
     * @param annonce L'annonce à ajouter
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public void ajouter(Annonce annonce) throws SQLException {
        if (annonce.getProprietaire() == null) {
            throw new SQLException("Proprietaire obligatoire pour une annonce.");
        }

        TypeAnnonce type = annonce.getType() != null ? annonce.getType() : TypeAnnonce.LOCATION;
        StatutAnnonce statut = annonce.getStatut() != null ? annonce.getStatut() : StatutAnnonce.DISPONIBLE;
        String unitePrix = annonce.getUnitePrix();
        if (unitePrix == null || unitePrix.isBlank()) {
            unitePrix = "jour";
        }

        String query = "INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, " +
                "categorie, marque, modele, annee_fabrication, localisation, proprietaire_id, " +
                "date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, annonce.getTitre());
            pst.setString(2, annonce.getDescription());
            pst.setString(3, type.name());
            pst.setString(4, statut.name());
            pst.setDouble(5, annonce.getPrix());
            pst.setString(6, unitePrix);
            pst.setString(7, annonce.getCategorie());
            pst.setString(8, annonce.getMarque());
            pst.setString(9, annonce.getModele());
            pst.setInt(10, annonce.getAnneeFabrication());
            pst.setString(11, annonce.getLocalisation());
            pst.setInt(12, annonce.getProprietaire().getId());
            pst.setDate(13,
                    annonce.getDateDebutDisponibilite() != null ? Date.valueOf(annonce.getDateDebutDisponibilite())
                            : null);
            pst.setDate(14,
                    annonce.getDateFinDisponibilite() != null ? Date.valueOf(annonce.getDateFinDisponibilite()) : null);
            pst.setBoolean(15, annonce.isAvecOperateur());
            pst.setDouble(16, annonce.getCaution());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                // Récupérer l'ID généré automatiquement
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        annonce.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Annonce ajoutée avec succès : " + annonce.getTitre());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'annonce : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Modifie une annonce existante dans la base de données.
     *
     * @param annonce L'annonce à modifier
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public void modifier(Annonce annonce) throws SQLException {
        TypeAnnonce type = annonce.getType() != null ? annonce.getType() : TypeAnnonce.LOCATION;
        StatutAnnonce statut = annonce.getStatut() != null ? annonce.getStatut() : StatutAnnonce.DISPONIBLE;
        String unitePrix = annonce.getUnitePrix();
        if (unitePrix == null || unitePrix.isBlank()) {
            unitePrix = "jour";
        }

        String query = "UPDATE annonces SET titre=?, description=?, type=?, statut=?, prix=?, unite_prix=?, " +
                "categorie=?, marque=?, modele=?, annee_fabrication=?, localisation=?, " +
                "date_debut_disponibilite=?, date_fin_disponibilite=?, avec_operateur=?, caution=? " +
                "WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, annonce.getTitre());
            pst.setString(2, annonce.getDescription());
            pst.setString(3, type.name());
            pst.setString(4, statut.name());
            pst.setDouble(5, annonce.getPrix());
            pst.setString(6, unitePrix);
            pst.setString(7, annonce.getCategorie());
            pst.setString(8, annonce.getMarque());
            pst.setString(9, annonce.getModele());
            pst.setInt(10, annonce.getAnneeFabrication());
            pst.setString(11, annonce.getLocalisation());
            pst.setDate(12,
                    annonce.getDateDebutDisponibilite() != null ? Date.valueOf(annonce.getDateDebutDisponibilite())
                            : null);
            pst.setDate(13,
                    annonce.getDateFinDisponibilite() != null ? Date.valueOf(annonce.getDateFinDisponibilite()) : null);
            pst.setBoolean(14, annonce.isAvecOperateur());
            pst.setDouble(15, annonce.getCaution());
            pst.setInt(16, annonce.getId());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Annonce modifiée avec succès : " + annonce.getTitre());
            } else {
                System.out.println("⚠️ Aucune annonce trouvée avec l'ID : " + annonce.getId());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'annonce : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Supprime une annonce de la base de données.
     *
     * @param annonce L'annonce à supprimer
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public void supprimer(Annonce annonce) throws SQLException {
        String query = "DELETE FROM annonces WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonce.getId());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Annonce supprimée avec succès : " + annonce.getTitre());
            } else {
                System.out.println("⚠️ Aucune annonce trouvée avec l'ID : " + annonce.getId());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'annonce : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère toutes les annonces de la base de données.
     *
     * @return Liste de toutes les annonces
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public List<Annonce> recuperer() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonces ORDER BY date_creation DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }

            System.out.println("✅ " + annonces.size() + " annonce(s) récupérée(s).");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des annonces : " + e.getMessage());
            throw e;
        }

        return annonces;
    }

    /**
     * Récupère une annonce par son identifiant.
     *
     * @param id L'identifiant de l'annonce
     * @return L'annonce trouvée, ou null si elle n'existe pas
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public Annonce recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM annonces WHERE id=?";
        Annonce annonce = null;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    annonce = mapResultSetToAnnonce(rs);
                    System.out.println("✅ Annonce trouvée : " + annonce.getTitre());
                } else {
                    System.out.println("⚠️ Aucune annonce trouvée avec l'ID : " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'annonce : " + e.getMessage());
            throw e;
        }

        return annonce;
    }

    /**
     * Récupère toutes les annonces disponibles.
     *
     * @return Liste des annonces disponibles
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Annonce> recupererDisponibles() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonces WHERE statut=? ORDER BY date_creation DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, StatutAnnonce.DISPONIBLE.name());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }

            System.out.println("✅ " + annonces.size() + " annonce(s) disponible(s) récupérée(s).");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des annonces : " + e.getMessage());
            throw e;
        }

        return annonces;
    }

    /**
     * Récupère toutes les annonces d'un propriétaire spécifique.
     *
     * @param proprietaireId L'identifiant du propriétaire
     * @return Liste des annonces du propriétaire
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Annonce> recupererParProprietaire(int proprietaireId) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonces WHERE proprietaire_id=? ORDER BY date_creation DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, proprietaireId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }

            System.out.println(
                    "✅ " + annonces.size() + " annonce(s) récupérée(s) pour le propriétaire ID: " + proprietaireId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des annonces : " + e.getMessage());
            throw e;
        }

        return annonces;
    }

    /**
     * Recherche des annonces par type.
     *
     * @param type Le type d'annonce recherché
     * @return Liste des annonces correspondantes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Annonce> rechercherParType(TypeAnnonce type) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonces WHERE type=? AND statut=? " +
                "ORDER BY date_creation DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, type.name());
            pst.setString(2, StatutAnnonce.DISPONIBLE.name());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }

            System.out.println("✅ " + annonces.size() + " annonce(s) trouvée(s) pour le type: " + type);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des annonces : " + e.getMessage());
            throw e;
        }

        return annonces;
    }

    /**
     * Méthode utilitaire pour mapper un ResultSet vers un objet Annonce.
     *
     * @param rs Le ResultSet à mapper
     * @return L'objet Annonce créé
     * @throws SQLException en cas d'erreur SQL
     */
    private Annonce mapResultSetToAnnonce(ResultSet rs) throws SQLException {
        Annonce annonce = new Annonce();
        annonce.setId(rs.getInt("id"));
        annonce.setTitre(rs.getString("titre"));
        annonce.setDescription(rs.getString("description"));
        annonce.setType(TypeAnnonce.valueOf(rs.getString("type")));
        annonce.setStatut(StatutAnnonce.valueOf(rs.getString("statut")));
        annonce.setPrix(rs.getDouble("prix"));
        annonce.setUnitePrix(rs.getString("unite_prix"));
        annonce.setCategorie(rs.getString("categorie"));
        annonce.setMarque(rs.getString("marque"));
        annonce.setModele(rs.getString("modele"));
        annonce.setAnneeFabrication(rs.getInt("annee_fabrication"));
        annonce.setLocalisation(rs.getString("localisation"));
        annonce.setLatitude(rs.getDouble("latitude"));
        annonce.setLongitude(rs.getDouble("longitude"));
        annonce.setAvecOperateur(rs.getBoolean("avec_operateur"));
        annonce.setAssuranceIncluse(rs.getBoolean("assurance_incluse"));
        annonce.setCaution(rs.getDouble("caution"));
        annonce.setConditionsLocation(rs.getString("conditions_location"));
        annonce.setQuantiteDisponible(rs.getInt("quantite_disponible"));
        annonce.setUniteQuantite(rs.getString("unite_quantite"));

        Date dateDebut = rs.getDate("date_debut_disponibilite");
        if (dateDebut != null) {
            annonce.setDateDebutDisponibilite(dateDebut.toLocalDate());
        }

        Date dateFin = rs.getDate("date_fin_disponibilite");
        if (dateFin != null) {
            annonce.setDateFinDisponibilite(dateFin.toLocalDate());
        }

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            annonce.setDateCreation(dateCreation.toLocalDateTime());
        }

        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            annonce.setDateModification(dateModification.toLocalDateTime());
        }

        // Récupération du propriétaire
        int proprietaireId = rs.getInt("proprietaire_id");
        try {
            User proprietaire = userService.recupererParId(proprietaireId);
            annonce.setProprietaire(proprietaire);
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de récupérer le propriétaire ID: " + proprietaireId);
        }

        // Récupération des photos depuis annonce_photos
        try {
            List<String> photos = new ArrayList<>();
            String photoQuery = "SELECT url_photo FROM annonce_photos WHERE annonce_id=? ORDER BY ordre";
            try (PreparedStatement photoPst = cnx.prepareStatement(photoQuery)) {
                photoPst.setInt(1, annonce.getId());
                try (ResultSet photoRs = photoPst.executeQuery()) {
                    while (photoRs.next()) {
                        String url = photoRs.getString("url_photo");
                        if (url != null && !url.trim().isEmpty()) {
                            photos.add(url);
                        }
                    }
                }
            }
            if (!photos.isEmpty()) {
                annonce.setPhotos(photos);
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de récupérer les photos pour annonce ID: " + annonce.getId());
        }

        return annonce;
    }
}
