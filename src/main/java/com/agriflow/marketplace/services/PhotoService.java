package com.agriflow.marketplace.services;

import com.agriflow.marketplace.models.PhotoAnnonce;
import com.agriflow.marketplace.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les photos 3D des annonces.
 * Permet d'ajouter, modifier, supprimer et récupérer les photos.
 */
public class PhotoService implements IService<PhotoAnnonce> {

    private Connection cnx;

    public PhotoService() {
        this.cnx = MyDatabase.getInstance().getCnx();
    }

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
                System.out.println("✅ Photo ajoutée pour l'annonce " + photo.getAnnonceId());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la photo : " + e.getMessage());
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
            System.out.println("✅ Photo modifiée : " + photo.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la photo : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(PhotoAnnonce photo) throws SQLException {
        String query = "DELETE FROM annonce_photos WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, photo.getId());
            pst.executeUpdate();
            System.out.println("✅ Photo supprimée : " + photo.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la photo : " + e.getMessage());
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

            System.out.println("✅ " + photos.size() + " photo(s) récupérée(s).");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des photos : " + e.getMessage());
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
            System.err.println("❌ Erreur lors de la récupération de la photo : " + e.getMessage());
            throw e;
        }

        return photo;
    }

    /**
     * Récupère toutes les photos d'une annonce spécifique, triées par ordre.
     *
     * @param annonceId ID de l'annonce
     * @return Liste des photos de l'annonce
     */
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

            System.out.println("✅ " + photos.size() + " photo(s) récupérée(s) pour l'annonce " + annonceId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des photos : " + e.getMessage());
            throw e;
        }

        return photos;
    }

    /**
     * Récupère la photo principale d'une annonce (ordre = 0).
     *
     * @param annonceId ID de l'annonce
     * @return La photo principale ou null
     */
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
            System.err.println("❌ Erreur lors de la récupération de la photo principale : " + e.getMessage());
            throw e;
        }

        return null;
    }

    /**
     * Supprime toutes les photos d'une annonce.
     *
     * @param annonceId ID de l'annonce
     */
    public void supprimerToutesPhotosAnnonce(int annonceId) throws SQLException {
        String query = "DELETE FROM annonce_photos WHERE annonce_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, annonceId);
            int nbSupprimes = pst.executeUpdate();
            System.out.println("✅ " + nbSupprimes + " photo(s) supprimée(s) pour l'annonce " + annonceId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression des photos : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ajoute plusieurs photos en une seule transaction.
     *
     * @param photos Liste de photos à ajouter
     */
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
                System.out.println("✅ " + results.length + " photo(s) ajoutée(s) en batch");
            }
        } catch (SQLException e) {
            cnx.rollback();
            System.err.println("❌ Erreur lors de l'ajout batch des photos : " + e.getMessage());
            throw e;
        } finally {
            cnx.setAutoCommit(true);
        }
    }

    /**
     * Compte le nombre de photos pour une annonce.
     *
     * @param annonceId ID de l'annonce
     * @return Nombre de photos
     */
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
            System.err.println("❌ Erreur lors du comptage des photos : " + e.getMessage());
            throw e;
        }

        return 0;
    }

    /**
     * Mappe un ResultSet vers un objet PhotoAnnonce.
     */
    private PhotoAnnonce mapResultSetToPhoto(ResultSet rs) throws SQLException {
        PhotoAnnonce photo = new PhotoAnnonce();
        photo.setId(rs.getInt("id"));
        photo.setAnnonceId(rs.getInt("annonce_id"));
        photo.setUrlPhoto(rs.getString("url_photo"));
        photo.setOrdre(rs.getInt("ordre"));
        return photo;
    }
}
