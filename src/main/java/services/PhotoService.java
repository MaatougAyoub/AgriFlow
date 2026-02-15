package services;

import entities.PhotoAnnonce;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Service CRUD mta3 les photos (table annonce_photos)
// kol annonce fih barcha photos, hedha el service yajoutihoum w yfas5houm
public class PhotoService implements IService<PhotoAnnonce> {

    private Connection cnx;

    public PhotoService() {
        this.cnx = MyDatabase.getInstance().getConnection();
    }

    // ===== AJOUTER PHOTO = INSERT INTO annonce_photos =====
    @Override
    public void ajouter(PhotoAnnonce photo) throws SQLException {
        String query = "INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES (?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, photo.getAnnonceId());
            pst.setString(2, photo.getUrlPhoto());
            pst.setInt(3, photo.getOrdre());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        photo.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Photo ajoutée pour annonce " + photo.getAnnonceId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajout photo : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(PhotoAnnonce photo) throws SQLException {
        String query = "UPDATE annonce_photos SET url_photo = ?, ordre = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, photo.getUrlPhoto());
            pst.setInt(2, photo.getOrdre());
            pst.setInt(3, photo.getId());

            pst.executeUpdate();
            System.out.println("Photo modifiée : " + photo.getId());
        } catch (SQLException e) {
            System.err.println("Erreur modification photo : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(PhotoAnnonce photo) throws SQLException {
        String query = "DELETE FROM annonce_photos WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, photo.getId());
            pst.executeUpdate();
            System.out.println("Photo supprimée : " + photo.getId());
        } catch (SQLException e) {
            System.err.println("Erreur suppression photo : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PhotoAnnonce> recuperer() throws SQLException {
        List<PhotoAnnonce> photos = new ArrayList<>();
        String query = "SELECT * FROM annonce_photos ORDER BY annonce_id, ordre";

        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                photos.add(mapResultSetToPhoto(rs));
            }

            System.out.println(photos.size() + " photo(s) récupérée(s)");
        } catch (SQLException e) {
            System.err.println("Erreur récupération photos : " + e.getMessage());
            throw e;
        }

        return photos;
    }

    @Override
    public PhotoAnnonce recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM annonce_photos WHERE id = ?";
        PhotoAnnonce photo = null;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    photo = mapResultSetToPhoto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération photo : " + e.getMessage());
            throw e;
        }

        return photo;
    }


    public List<PhotoAnnonce> recupererParAnnonce(int annonceId) throws SQLException {
        List<PhotoAnnonce> photos = new ArrayList<>();
        String query = "SELECT * FROM annonce_photos WHERE annonce_id = ? ORDER BY ordre ASC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonceId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    photos.add(mapResultSetToPhoto(rs));
                }
            }

            System.out.println(photos.size() + " photo(s) pour annonce " + annonceId);
        } catch (SQLException e) {
            System.err.println("Erreur récupération photos annonce : " + e.getMessage());
            throw e;
        }

        return photos;
    }


    public PhotoAnnonce recupererPhotoPrincipale(int annonceId) throws SQLException {
        String query = "SELECT * FROM annonce_photos WHERE annonce_id = ? AND ordre = 0 LIMIT 1";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonceId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhoto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération photo principale : " + e.getMessage());
            throw e;
        }

        return null;
    }


    public void supprimerToutesPhotosAnnonce(int annonceId) throws SQLException {
        String query = "DELETE FROM annonce_photos WHERE annonce_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonceId);
            int nbSupprimes = pst.executeUpdate();
            System.out.println(nbSupprimes + " photo(s) supprimée(s) pour annonce " + annonceId);
        } catch (SQLException e) {
            System.err.println("Erreur suppression photos : " + e.getMessage());
            throw e;
        }
    }


    // ===== AJOUTER BARCHA PHOTOS D'UN COUP (BATCH) =====
    // nesta3mlou batch insert bech nzidou barcha photos f wa9t wa7ed
    // autoCommit=false bech ken fih erreur, nraj3ou kol chay (rollback)
    public void ajouterPlusieursPhotos(List<PhotoAnnonce> photos) throws SQLException {
        String query = "INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES (?, ?, ?)";

        try {
            cnx.setAutoCommit(false);

            try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (PhotoAnnonce photo : photos) {
                    pst.setInt(1, photo.getAnnonceId());
                    pst.setString(2, photo.getUrlPhoto());
                    pst.setInt(3, photo.getOrdre());
                    pst.addBatch();
                }

                int[] results = pst.executeBatch();
                cnx.commit();
                System.out.println(results.length + " photo(s) ajoutée(s) en batch");
            }
        } catch (SQLException e) {
            cnx.rollback();
            System.err.println("Erreur ajout batch photos : " + e.getMessage());
            throw e;
        } finally {
            cnx.setAutoCommit(true);
        }
    }


    public int compterPhotosAnnonce(int annonceId) throws SQLException {
        String query = "SELECT COUNT(*) FROM annonce_photos WHERE annonce_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonceId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage photos : " + e.getMessage());
            throw e;
        }

        return 0;
    }

    // n7awlou el ResultSet l objet PhotoAnnonce (mapping)
    private PhotoAnnonce mapResultSetToPhoto(ResultSet rs) throws SQLException {
        PhotoAnnonce photo = new PhotoAnnonce();
        photo.setId(rs.getInt("id"));
        photo.setAnnonceId(rs.getInt("annonce_id"));
        photo.setUrlPhoto(rs.getString("url_photo"));
        photo.setOrdre(rs.getInt("ordre"));
        return photo;
    }
}