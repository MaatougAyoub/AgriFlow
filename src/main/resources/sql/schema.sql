-- ============================================
-- AGRIFLOW MARKETPLACE - Base de Données
-- Sprint 0 - Validation Académique JDBC
-- ============================================

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS agriflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agriflow;

-- ============================================
-- TABLE: USERS (Utilisateurs)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    region VARCHAR(100),
    photo_profil VARCHAR(255),
    signature_image LONGBLOB,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: ANNONCES (Annonces de Matériel)
-- ============================================
CREATE TABLE IF NOT EXISTS annonces (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type ENUM('LOCATION', 'VENTE') DEFAULT 'LOCATION',
    statut ENUM('DISPONIBLE', 'RESERVEE', 'LOUEE', 'VENDUE', 'SUSPENDUE') DEFAULT 'DISPONIBLE',
    prix DOUBLE NOT NULL,
    unite_prix VARCHAR(50) DEFAULT 'jour',
    categorie VARCHAR(100),
    marque VARCHAR(100),
    modele VARCHAR(100),
    annee_fabrication INT,
    localisation VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    proprietaire_id INT NOT NULL,
    date_debut_disponibilite DATE,
    date_fin_disponibilite DATE,
    avec_operateur BOOLEAN DEFAULT FALSE,
    assurance_incluse BOOLEAN DEFAULT FALSE,
    caution DOUBLE DEFAULT 0,
    conditions_location TEXT,
    quantite_disponible INT DEFAULT 1,
    unite_quantite VARCHAR(50),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (proprietaire_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_type (type),
    INDEX idx_statut (statut),
    INDEX idx_proprietaire (proprietaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: RESERVATIONS (Réservations P2P)
-- ============================================
CREATE TABLE IF NOT EXISTS reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    annonce_id INT NOT NULL,
    demandeur_id INT NOT NULL,
    proprietaire_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    quantite INT DEFAULT 1,
    prix_total DOUBLE NOT NULL,
    commission DOUBLE DEFAULT 0,
    caution DOUBLE DEFAULT 0,
    statut ENUM('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'EN_COURS', 'TERMINEE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    message_demande TEXT,
    reponse_proprietaire TEXT,
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_reponse TIMESTAMP NULL,
    contrat_url VARCHAR(255),
    contrat_signe BOOLEAN DEFAULT FALSE,
    date_signature_contrat TIMESTAMP NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annonce_id) REFERENCES annonces(id) ON DELETE CASCADE,
    FOREIGN KEY (demandeur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (proprietaire_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_demandeur (demandeur_id),
    INDEX idx_proprietaire (proprietaire_id),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: MESSAGES (Messagerie P2P)
-- ============================================
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    sujet VARCHAR(200),
    contenu TEXT NOT NULL,
    annonce_id INT NULL,
    reservation_id INT NULL,
    lu BOOLEAN DEFAULT FALSE,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_lecture TIMESTAMP NULL,
    FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataire_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (annonce_id) REFERENCES annonces(id) ON DELETE SET NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE SET NULL,
    INDEX idx_destinataire (destinataire_id),
    INDEX idx_lu (lu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: PHOTOS_ANNONCES (Photos des Annonces)
-- ============================================
CREATE TABLE IF NOT EXISTS photos_annonces (
    id INT PRIMARY KEY AUTO_INCREMENT,
    annonce_id INT NOT NULL,
    url VARCHAR(255) NOT NULL,
    ordre INT DEFAULT 0,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annonce_id) REFERENCES annonces(id) ON DELETE CASCADE,
    INDEX idx_annonce (annonce_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- DONNÉES DE TEST - Sprint 0
-- ============================================

-- Insertion des 3 utilisateurs de test
INSERT INTO users (nom, prenom, email, telephone, adresse, region) VALUES
('Jerbi', 'Amenallah', 'amenallah@agriflow.tn', '+216 20 123 456', '15 Avenue Habib Bourguiba', 'Sousse'),
('Ben Ali', 'Fatma', 'fatma.benali@agriflow.tn', '+216 22 345 678', '42 Rue de la République', 'Sfax'),
('Trabelsi', 'Mohamed', 'mohamed.trabelsi@agriflow.tn', '+216 98 765 432', '8 Avenue Ali Belhouane', 'Kairouan');

-- Insertion des 5 annonces (Tracteurs et Moissonneuses)
INSERT INTO annonces (
    titre, 
    description, 
    type, 
    statut,
    prix, 
    unite_prix, 
    categorie, 
    marque, 
    modele, 
    annee_fabrication, 
    localisation, 
    latitude, 
    longitude, 
    proprietaire_id, 
    date_debut_disponibilite, 
    date_fin_disponibilite, 
    avec_operateur, 
    assurance_incluse,
    caution,
    conditions_location
) VALUES
-- Annonce 1: Tracteur John Deere
(
    'Tracteur John Deere 6130R - 130CV avec GPS', 
    'Tracteur agricole moderne de 130 chevaux, équipé de climatisation, GPS intégré RTK pour guidage automatique. Parfait pour labour profond, semis de précision et travaux polyvalents. Entretien suivi par concessionnaire officiel. Disponible avec ou sans opérateur qualifié.', 
    'LOCATION',
    'DISPONIBLE',
    250.00, 
    'jour', 
    'Tracteur', 
    'John Deere', 
    '6130R', 
    2020, 
    'Sousse - Zone Industrielle', 
    35.8256, 
    10.6369, 
    1, 
    '2025-03-01', 
    '2025-09-30', 
    TRUE,
    TRUE,
    2000.00,
    'Carburant à la charge du locataire. Restitution avec réservoir plein. Assurance dommages incluse.'
),

-- Annonce 2: Moissonneuse CLAAS
(
    'Moissonneuse-batteuse CLAAS Lexion 760 - 8m', 
    'Moissonneuse-batteuse performante avec barre de coupe de 8 mètres. Idéale pour récolte de céréales (blé, orge). Système de nettoyage ultra-performant. Cabine climatisée avec ordinateur de bord. Révision complète effectuée avant saison. Opérateur expérimenté inclus.', 
    'LOCATION',
    'DISPONIBLE',
    800.00, 
    'jour', 
    'Moissonneuse', 
    'CLAAS', 
    'Lexion 760', 
    2019, 
    'Sfax - Route de Tunis', 
    34.7406, 
    10.7603, 
    2, 
    '2025-06-01', 
    '2025-08-31', 
    TRUE,
    TRUE,
    5000.00,
    'Location minimum 3 jours. Opérateur obligatoire (inclus). Carburant et transport à la charge du locataire.'
),

-- Annonce 3: Tracteur Massey Ferguson
(
    'Tracteur Massey Ferguson 5710 - 110CV', 
    'Tracteur polyvalent 110 chevaux avec relevage hydraulique avant et arrière. Très bon état général, entretien régulier chez concessionnaire. Idéal pour maraîchage, arboriculture et travaux de fenaison. Équipé de pneus récents et attelage trois points.', 
    'LOCATION',
    'DISPONIBLE',
    200.00, 
    'jour', 
    'Tracteur', 
    'Massey Ferguson', 
    '5710', 
    2018, 
    'Kairouan - Route de Sousse Km 4', 
    35.6781, 
    10.0963, 
    3, 
    '2025-03-15', 
    '2025-10-31', 
    FALSE,
    FALSE,
    1500.00,
    'Caution restituée sous 48h après contrôle. Permis tracteur requis. Remorque disponible en option (+50 DT/jour).'
),

-- Annonce 4: Tracteur New Holland À VENDRE
(
    'Tracteur New Holland T7.270 - 270CV [À VENDRE]', 
    'Puissant tracteur de 270 chevaux pour gros travaux agricoles et travaux lourds. Transmission automatique Ultra Command. Relevage avant et arrière 10 tonnes. Cabine Horizon avec suspension pneumatique. 4800 heures. Carnet d''entretien complet. Prix négociable. Possibilité de reprise.', 
    'VENTE',
    'DISPONIBLE',
    85000.00, 
    'unité', 
    'Tracteur', 
    'New Holland', 
    'T7.270', 
    2017, 
    'Sousse - Ferme El Karma', 
    35.8256, 
    10.6369, 
    1, 
    NULL, 
    NULL, 
    FALSE,
    FALSE,
    0,
    'Vente avec facture et certificat de conformité. Garantie 6 mois pièces. Livraison possible (coût selon distance).'
),

-- Annonce 5: Moissonneuse John Deere Premium
(
    'Moissonneuse John Deere S780 - 10.7m Premium', 
    'Moissonneuse-batteuse haut de gamme avec barre de coupe Draper de 10.7 mètres. Technologie ProDrive pour performance maximale. Système de nettoyage cascade 3D. Capteurs intelligents de pertes. Cabine luxe avec écran tactile 4600 CommandCenter. État impeccable. Opérateur certifié John Deere inclus.', 
    'LOCATION',
    'DISPONIBLE',
    950.00, 
    'jour', 
    'Moissonneuse', 
    'John Deere', 
    'S780', 
    2021, 
    'Sfax - Zone Agricole El Hencha', 
    34.7406, 
    10.7603, 
    2, 
    '2025-06-15', 
    '2025-09-15', 
    TRUE,
    TRUE,
    6000.00,
    'Location minimum 5 jours. Tarifs dégressifs pour longue durée. Opérateur expert inclus. Carburant à la charge du locataire. Transport aller-retour : 500 DT.'
);

-- ============================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- ============================================
SELECT '✅ Base de données AgriFlow créée avec succès!' AS '═══════════════════════════════════════';
SELECT '' AS '';
SELECT CONCAT('👤 ', COUNT(*), ' utilisateurs insérés avec succès') AS 'USERS' FROM users;
SELECT CONCAT('📢 ', COUNT(*), ' annonces disponibles pour validation') AS 'ANNONCES' FROM annonces;
SELECT CONCAT('📊 ', COUNT(*), ' annonces en LOCATION') AS 'LOCATIONS' FROM annonces WHERE type = 'LOCATION';
SELECT CONCAT('💰 ', COUNT(*), ' annonces en VENTE') AS 'VENTES' FROM annonces WHERE type = 'VENTE';
SELECT '' AS '';
SELECT '🎯 Application prête pour Sprint 0 !' AS '═══════════════════════════════════════';

-- ============================================
-- STATISTIQUES POUR VALIDATION
-- ============================================
SELECT 
    '📊 RÉCAPITULATIF SPRINT 0' AS '',
    '' AS ' ',
    'Base de données: agriflow' AS 'Configuration',
    '5 tables créées (users, annonces, reservations, messages, photos_annonces)' AS 'Structure',
    '3 utilisateurs + 5 annonces de test' AS 'Données',
    'PreparedStatement sur toutes les requêtes' AS 'Sécurité JDBC',
    'Pattern Singleton (MyDatabase)' AS 'Architecture',
    'Commission 10% automatique' AS 'Business Logic'
FROM DUAL;
