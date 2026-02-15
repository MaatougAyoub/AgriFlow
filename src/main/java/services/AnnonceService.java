package services;

import entities.*;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Service CRUD mta3 les Annonces - houni na3mlou kol les operations (ajouter, modifier, supprimer, recuperer)
// yimplementi IService<Annonce> ya3ni lazem y7ot les 4 methodes CRUD
public class AnnonceService implements IService<Annonce> {

    private Connection cnx; // el connexion mta3 la base
    private final UserService userService; // bech nrec5periw l user mta3 kol annonce


    public AnnonceService() {
        // njibou el connexion men el Singleton MyDatabase
        this.cnx = MyDatabase.getInstance().getConnection();
        this.userService = new UserService();
    }


    // ===== AJOUTER = INSERT INTO annonces =====
    // na3mlou INSERT fl base, nesta3mlou PreparedStatement bech na7miw men SQL Injection
    // les ? yet3awdhou bel valeurs (setString, setInt...)
    @Override
    public void ajouter(Annonce annonce) throws SQLException {
        if (annonce.getProprietaire() == null) {
            throw new SQLException("Lezem femma proprietaire, sinon chkoun bech ybi3 ?");
        }

        // valeurs par defaut ken ma7athomch
        TypeAnnonce type = annonce.getType() != null ? annonce.getType() : TypeAnnonce.LOCATION;
        StatutAnnonce statut = annonce.getStatut() != null ? annonce.getStatut() : StatutAnnonce.DISPONIBLE;
        String unitePrix = annonce.getUnitePrix();
        if (unitePrix == null || unitePrix.isBlank()) {
            unitePrix = "jour";
        }

        // el query INSERT bel ? (PreparedStatement ya7mi men SQL Injection)
        String query = "INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, " +
                "categorie, marque, modele, annee_fabrication, localisation, proprietaire_id, " +
                "date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // RETURN_GENERATED_KEYS bech ba3d l INSERT, nraj3ou l ID elli tkhla9 (auto_increment)
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
                // Nraj3ou l ID elli tkhla9 tawa (auto-increment)
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        annonce.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Annonce ajoutée : " + annonce.getTitre());
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajout annonce : " + e.getMessage());
            throw e;
        }
    }


    // ===== MODIFIER = UPDATE annonces SET ... WHERE id=? =====
    // nbadlou les valeurs mta3 annonce deja mawjouda fl base
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
                System.out.println("Annonce modifiée : " + annonce.getTitre());
            } else {
                System.out.println("Aucune annonce trouvée avec ID : " + annonce.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur modification annonce : " + e.getMessage());
            throw e;
        }
    }


    // ===== SUPPRIMER = DELETE FROM annonces WHERE id=? =====
    // nfas5ou l annonce mel base b el ID mta3ha
    @Override
    public void supprimer(Annonce annonce) throws SQLException {
        String query = "DELETE FROM annonces WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonce.getId());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Annonce supprimée : " + annonce.getTitre());
            } else {
                System.out.println("Aucune annonce trouvée avec ID : " + annonce.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur suppression annonce : " + e.getMessage());
            throw e;
        }
    }


    // ===== RECUPERER TOUT = SELECT * FROM annonces =====
    // njibou lkol les annonces mel base, trié bel date (le plus recent l foug)
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

            System.out.println(annonces.size() + " annonce(s) récupérée(s)");
        } catch (SQLException e) {
            System.err.println("Erreur récupération annonces : " + e.getMessage());
            throw e;
        }

        return annonces;
    }


    // ===== RECUPERER PAR ID = SELECT * FROM annonces WHERE id=? =====
    // njibou annonce wa7da precisa bel ID mta3ha
    @Override
    public Annonce recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM annonces WHERE id=?";
        Annonce annonce = null;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    annonce = mapResultSetToAnnonce(rs);
                    System.out.println("Annonce trouvée : " + annonce.getTitre());
                } else {
                    System.out.println("Aucune annonce avec ID : " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération annonce : " + e.getMessage());
            throw e;
        }

        return annonce;
    }


    // njibou ken les annonces DISPONIBLES (elli mazelt ma tkraw/tbaw)
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

            System.out.println(annonces.size() + " annonce(s) disponible(s)");
        } catch (SQLException e) {
            System.err.println("Erreur récupération annonces dispo : " + e.getMessage());
            throw e;
        }

        return annonces;
    }


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

            System.out.println(annonces.size() + " annonce(s) pour proprietaire ID: " + proprietaireId);
        } catch (SQLException e) {
            System.err.println("Erreur récupération par proprietaire : " + e.getMessage());
            throw e;
        }

        return annonces;
    }


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

            System.out.println(annonces.size() + " annonce(s) de type " + type);
        } catch (SQLException e) {
            System.err.println("Erreur recherche par type : " + e.getMessage());
            throw e;
        }

        return annonces;
    }

    // Houni n7awlou el ResultSet (elli jey mel base) l objet Annonce Java
    // ya3ni kol colonne fl base -> attribut fl objet (mapping)
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

        // njibou el proprietaire men table utilisateurs (JOIN manuelle)
        int proprietaireId = rs.getInt("proprietaire_id");
        try {
            User proprietaire = userService.recupererParId(proprietaireId);
            annonce.setProprietaire(proprietaire);
        } catch (SQLException e) {
            System.err.println("Proprietaire introuvable ID: " + proprietaireId);
        }

        // njibou les photos mel table annonce_photos (SELECT bel annonce_id)
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
            System.err.println("Photos introuvables pour annonce ID: " + annonce.getId());
        }

        return annonce;
    }
}