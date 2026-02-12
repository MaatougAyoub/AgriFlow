-- =====================================================
-- AGRIFLOW - Script de création de la base de données
-- Module: MARKETPLACE (Location & Vente)
-- Créé par: Amenallah Jerbi - TeamSpark
-- Date: 2026
-- =====================================================

CREATE DATABASE IF NOT EXISTS agriflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agriflow_db;

-- =====================================================
-- TABLE: users
-- Gérée par le module Gestion Utilisateurs (Ayoub)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    region VARCHAR(100),
    photo_profil VARCHAR(255),
    signature_image LONGBLOB,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    INDEX idx_region (region),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: annonces
-- Description: Annonces de location et vente agricole
-- =====================================================
CREATE TABLE IF NOT EXISTS annonces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type ENUM('LOCATION', 'VENTE') NOT NULL,
    statut ENUM('DISPONIBLE', 'RESERVEE', 'LOUEE', 'VENDUE', 'EXPIREE') DEFAULT 'DISPONIBLE',
    prix DECIMAL(10, 2) NOT NULL,
    unite_prix VARCHAR(20) DEFAULT 'jour',
    categorie VARCHAR(100),
    marque VARCHAR(100),
    modele VARCHAR(100),
    annee_fabrication INT,
    localisation VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    proprietaire_id INT NOT NULL,
    date_debut_disponibilite DATE,
    date_fin_disponibilite DATE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    avec_operateur BOOLEAN DEFAULT FALSE,
    assurance_incluse BOOLEAN DEFAULT FALSE,
    caution DECIMAL(10, 2) DEFAULT 0,
    conditions_location TEXT,
    quantite_disponible INT DEFAULT 0,
    unite_quantite VARCHAR(20) DEFAULT 'kg',
    CONSTRAINT fk_annonce_proprietaire FOREIGN KEY (proprietaire_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_type (type),
    INDEX idx_statut (statut),
    INDEX idx_categorie (categorie),
    INDEX idx_prix (prix),
    INDEX idx_proprietaire (proprietaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: annonce_photos
-- =====================================================
CREATE TABLE IF NOT EXISTS annonce_photos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    annonce_id INT NOT NULL,
    url_photo VARCHAR(500) NOT NULL,
    ordre INT DEFAULT 0,
    CONSTRAINT fk_photo_annonce FOREIGN KEY (annonce_id) REFERENCES annonces(id) ON DELETE CASCADE,
    INDEX idx_annonce (annonce_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: reservations
-- Architecture P2P - PAS de validation admin
-- =====================================================
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    annonce_id INT NOT NULL,
    demandeur_id INT NOT NULL,
    proprietaire_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    quantite INT DEFAULT 1,
    prix_total DECIMAL(10, 2) NOT NULL,
    caution DECIMAL(10, 2) DEFAULT 0,
    statut ENUM('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'EN_COURS', 'TERMINEE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    message_demande TEXT,
    reponse_proprietaire TEXT,
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_reponse TIMESTAMP NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    contrat_url VARCHAR(500),
    contrat_signe BOOLEAN DEFAULT FALSE,
    date_signature_contrat TIMESTAMP NULL,
    paiement_effectue BOOLEAN DEFAULT FALSE,
    date_paiement TIMESTAMP NULL,
    mode_paiement VARCHAR(50),
    CONSTRAINT fk_reservation_annonce FOREIGN KEY (annonce_id) REFERENCES annonces(id),
    CONSTRAINT fk_reservation_demandeur FOREIGN KEY (demandeur_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_proprietaire FOREIGN KEY (proprietaire_id) REFERENCES users(id),
    INDEX idx_statut (statut),
    INDEX idx_demandeur (demandeur_id),
    INDEX idx_proprietaire (proprietaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: messages
-- =====================================================
CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    sujet VARCHAR(255),
    contenu TEXT NOT NULL,
    annonce_id INT NULL,
    reservation_id INT NULL,
    lu BOOLEAN DEFAULT FALSE,
    date_lecture TIMESTAMP NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_expediteur FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_destinataire FOREIGN KEY (destinataire_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_annonce FOREIGN KEY (annonce_id) REFERENCES annonces(id) ON DELETE SET NULL,
    CONSTRAINT fk_message_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE SET NULL,
    INDEX idx_expediteur (expediteur_id),
    INDEX idx_destinataire (destinataire_id),
    INDEX idx_lu (lu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- DONNÉES DE TEST
-- =====================================================
INSERT INTO users (nom, prenom, email, password, telephone, adresse, region) VALUES
('Jerbi', 'Amenallah', 'amenallah@agriflow.tn', 'pass123', '+216 20 123 456', 'Sousse', 'Sousse'),
('Ben Ali', 'Oussema', 'oussema@agriflow.tn', 'pass123', '+216 21 234 567', 'Tunis', 'Tunis'),
('Trabelsi', 'Yakine', 'yakine@agriflow.tn', 'pass123', '+216 22 345 678', 'Sfax', 'Sfax');

INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, categorie, marque, modele, annee_fabrication, localisation, proprietaire_id, date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution) VALUES
('Tracteur John Deere 6120M', 'Tracteur puissant, entretien régulier', 'LOCATION', 'DISPONIBLE', 250.00, 'jour', 'Tracteur', 'John Deere', '6120M', 2020, 'Sousse', 1, '2026-02-01', '2026-06-30', TRUE, 1000.00),
('Moissonneuse New Holland CR9080', 'Moissonneuse dernière génération', 'LOCATION', 'DISPONIBLE', 800.00, 'jour', 'Moissonneuse', 'New Holland', 'CR9080', 2019, 'Sfax', 2, '2026-03-01', '2026-05-31', TRUE, 3000.00),
('Engrais NPK 20-20-20', '50 sacs de 50kg disponibles', 'VENTE', 'DISPONIBLE', 85.00, 'sac', 'Engrais', 'SIAPE', 'NPK 20-20-20', NULL, 'Tunis', 3, '2026-01-01', '2026-12-31', FALSE, 0.00);

-- Photos de test (images libres de droits)
INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES
(1, 'https://images.unsplash.com/photo-1530267981375-f0de937f5f13?w=400&h=250&fit=crop', 0),
(2, 'https://images.unsplash.com/photo-1574943320219-553eb213f72d?w=400&h=250&fit=crop', 0),
(3, 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=250&fit=crop', 0);

SELECT 'Base AGRIFLOW créée avec succès!' AS message;
