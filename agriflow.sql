-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 14, 2026 at 10:15 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `agriflow`
--
CREATE DATABASE IF NOT EXISTS `agriflow` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `agriflow`;

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `cin` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motDePasse` varchar(255) NOT NULL,
  `role` varchar(40) NOT NULL,
  `dateCreation` date NOT NULL,
  `signature` varchar(500) NOT NULL,
  `revenu` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `revenu`) VALUES
(38, 'Maatoug', 'Ayoub', 585, 'ayoub.maatoug@esprit.tn', 'pwayoub', 'ADMIN', '2026-02-13', 'uploads/signatures/1771084885464_Gemini_Generated_Image_mq403hmq403hmq40.png', 200.5);

-- --------------------------------------------------------

--
-- Table structure for table `agriculteurs`
--

CREATE TABLE `agriculteurs` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `cin` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motDePasse` varchar(255) NOT NULL,
  `role` varchar(40) NOT NULL,
  `dateCreation` date NOT NULL,
  `signature` varchar(500) NOT NULL,
  `carte_pro` varchar(500) DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `parcelles` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `agriculteurs`
--

INSERT INTO `agriculteurs` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `carte_pro`, `adresse`, `parcelles`) VALUES
(36, 'Sami', 'Maatoug', 2030501, 'sami@gmail.com', 'sami*11', 'AGRICULTEUR', '2026-02-10', 'uploads/signatures/1770850459714_TuNour.jpg', 'uploads/cartes_pro/1770981259197_بطاقة الفلاح.jpg', 'rasjebel', '');

-- --------------------------------------------------------

--
-- Table structure for table `annonces`
--

CREATE TABLE `annonces` (
  `id` int(11) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `type` enum('LOCATION','VENTE') NOT NULL,
  `statut` enum('DISPONIBLE','RESERVEE','LOUEE','VENDUE','EXPIREE') DEFAULT 'DISPONIBLE',
  `prix` decimal(10,2) NOT NULL,
  `unite_prix` varchar(20) DEFAULT 'jour',
  `categorie` varchar(100) DEFAULT NULL,
  `marque` varchar(100) DEFAULT NULL,
  `modele` varchar(100) DEFAULT NULL,
  `annee_fabrication` int(11) DEFAULT NULL,
  `localisation` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `proprietaire_id` int(11) NOT NULL,
  `date_debut_disponibilite` date DEFAULT NULL,
  `date_fin_disponibilite` date DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `date_modification` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `avec_operateur` tinyint(1) DEFAULT 0,
  `assurance_incluse` tinyint(1) DEFAULT 0,
  `caution` decimal(10,2) DEFAULT 0.00,
  `conditions_location` text DEFAULT NULL,
  `quantite_disponible` int(11) DEFAULT 0,
  `unite_quantite` varchar(20) DEFAULT 'kg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `annonces`
--

INSERT INTO `annonces` (`id`, `titre`, `description`, `type`, `statut`, `prix`, `unite_prix`, `categorie`, `marque`, `modele`, `annee_fabrication`, `localisation`, `latitude`, `longitude`, `proprietaire_id`, `date_debut_disponibilite`, `date_fin_disponibilite`, `date_creation`, `date_modification`, `avec_operateur`, `assurance_incluse`, `caution`, `conditions_location`, `quantite_disponible`, `unite_quantite`) VALUES
(1, 'Tracteur John Deere 6120M', 'Tracteur puissant 120CV, entretien régulier chez concessionnaire. GPS intégré, climatisation. Idéal pour labour et semis.', 'LOCATION', 'DISPONIBLE', 250.00, 'jour', 'Tracteur', 'John Deere', '6120M', 2020, 'Sousse', 35.82560000, 10.63690000, 36, '2026-02-01', '2026-06-30', '2026-02-14 21:10:59', '2026-02-14 21:10:59', 1, 1, 1000.00, 'Carburant à la charge du locataire. Restitution avec réservoir plein.', 1, 'unité'),
(2, 'Moissonneuse New Holland CR9080', 'Moissonneuse-batteuse dernière génération avec barre de coupe 7m. Système de nettoyage performant. Opérateur expérimenté inclus.', 'LOCATION', 'DISPONIBLE', 800.00, 'jour', 'Moissonneuse', 'New Holland', 'CR9080', 2019, 'Sfax', 34.74060000, 10.76030000, 36, '2026-03-01', '2026-05-31', '2026-02-14 21:10:59', '2026-02-14 21:10:59', 1, 1, 3000.00, 'Location minimum 3 jours. Opérateur inclus. Carburant à la charge du locataire.', 1, 'unité'),
(3, 'Engrais NPK 20-20-20 - 50 sacs', '50 sacs de 50kg disponibles. Engrais équilibré NPK pour cultures maraîchères et céréalières. Livraison possible.', 'VENTE', 'DISPONIBLE', 85.00, 'sac', 'Engrais', 'SIAPE', 'NPK 20-20-20', 2026, 'Tunis', 36.80650000, 10.18150000, 39, '2026-01-01', '2026-12-31', '2026-02-14 21:10:59', '2026-02-14 21:10:59', 0, 0, 0.00, 'Vente par lot. Prix unitaire par sac de 50kg.', 50, 'sac');

-- --------------------------------------------------------

--
-- Table structure for table `annonce_photos`
--

CREATE TABLE `annonce_photos` (
  `id` int(11) NOT NULL,
  `annonce_id` int(11) NOT NULL,
  `url_photo` varchar(500) NOT NULL,
  `ordre` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `annonce_photos`
--

INSERT INTO `annonce_photos` (`id`, `annonce_id`, `url_photo`, `ordre`) VALUES
(1, 1, 'https://images.unsplash.com/photo-1530267981375-f0de937f5f13?w=400&h=250&fit=crop', 0),
(2, 2, 'https://images.unsplash.com/photo-1574943320219-553eb213f72d?w=400&h=250&fit=crop', 0),
(3, 3, 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=250&fit=crop', 0);

-- --------------------------------------------------------

--
-- Table structure for table `collab_applications`
--

CREATE TABLE `collab_applications` (
  `id` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `candidate_id` int(11) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `applied_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_applications`
--

INSERT INTO `collab_applications` (`id`, `request_id`, `candidate_id`, `message`, `status`, `applied_at`) VALUES
(1, 1, 36, 'Expert en récolte, disponible ces dates', 'APPROVED', '2026-02-07 15:40:22'),
(2, 1, 37, 'Je suis disponible et j\'ai de l\'expérience', 'PENDING', '2026-02-07 15:40:22'),
(3, 2, 35, 'Intéressé par cette mission', 'PENDING', '2026-02-07 15:40:22');

-- --------------------------------------------------------

--
-- Table structure for table `collab_requests`
--

CREATE TABLE `collab_requests` (
  `id` bigint(20) NOT NULL,
  `requester_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `needed_people` int(11) DEFAULT 1,
  `status` enum('PENDING','APPROVED','REJECTED','CLOSED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_requests`
--

INSERT INTO `collab_requests` (`id`, `requester_id`, `title`, `description`, `start_date`, `end_date`, `needed_people`, `status`, `created_at`, `updated_at`) VALUES
(1, 36, 'Récolte olives - Nabeul', 'Besoin de 2 personnes pour récolte olives bio', '2026-02-10', '2026-02-15', 2, 'APPROVED', '2026-02-07 15:40:22', '2026-02-12 11:10:11'),
(2, 37, 'Plantation tomates - Bizerte', 'Recherche 3 agriculteurs expérimentés pour plantation tomates', '2026-03-01', '2026-03-05', 3, 'REJECTED', '2026-02-07 15:40:22', '2026-02-14 18:46:55'),
(3, 38, 'Irrigation orangers - Sousse', 'Besoin expertise irrigation goutte-à-goutte', '2026-02-20', '2026-02-22', 1, 'REJECTED', '2026-02-07 15:40:22', '2026-02-12 11:10:22');

-- --------------------------------------------------------

--
-- Table structure for table `cultures`
--

CREATE TABLE `cultures` (
  `id` int(11) NOT NULL,
  `parcelle_id` int(11) NOT NULL,
  `proprietaire_id` int(11) NOT NULL,
  `nom` varchar(150) DEFAULT NULL,
  `type_culture` enum('BLE','ORGE','MAIS','POMME_DE_TERRE','TOMATE','OLIVIER','AGRUMES','VIGNE','PASTECQUE','FRAISE','LEGUMES','AUTRE') DEFAULT 'AUTRE',
  `superficie` decimal(10,2) DEFAULT NULL,
  `etat` enum('EN_COURS','RECOLTEE','EN_VENTE','VENDUE') DEFAULT 'EN_COURS',
  `date_recolte` date DEFAULT NULL,
  `recolte_estime` decimal(10,2) DEFAULT NULL,
  `date_creation` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `culture_vendue`
--

CREATE TABLE `culture_vendue` (
  `id_vente` int(11) NOT NULL,
  `id_culture` int(11) NOT NULL,
  `id_acheteur` int(11) DEFAULT NULL,
  `date_vente` date DEFAULT NULL,
  `date_publication` datetime NOT NULL DEFAULT current_timestamp(),
  `prix_vente` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `experts`
--

CREATE TABLE `experts` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `cin` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motDePasse` varchar(255) NOT NULL,
  `role` varchar(40) NOT NULL,
  `dateCreation` date NOT NULL,
  `signature` varchar(500) NOT NULL,
  `certification` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `experts`
--

INSERT INTO `experts` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `certification`) VALUES
(35, 'oussama', 'Fattoumi', 88554411, 'oussama@gmail.com', 'oussama1', 'EXPERT', '2026-02-10', 'uploadssignatures1770758047802_Logo_ESPRIT_Ariana.jpg', 'uploads/certifications/1770898294372_diplome expert(Oussama).png'),
(37, 'Ayoub22', 'Maatoug22', 11429920, 'maatougayoub7@gmail.com', 'pwayoub', 'EXPERT', '2026-02-11', 'uploadssignatures1770843103211_user.png', 'uploadscertifications1770843118809_diplome expert(Oussama).png');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `expediteur_id` int(11) NOT NULL,
  `destinataire_id` int(11) NOT NULL,
  `sujet` varchar(255) DEFAULT NULL,
  `contenu` text NOT NULL,
  `annonce_id` int(11) DEFAULT NULL,
  `reservation_id` int(11) DEFAULT NULL,
  `lu` tinyint(1) DEFAULT 0,
  `date_lecture` timestamp NULL DEFAULT NULL,
  `date_envoi` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `parcelle`
--

CREATE TABLE `parcelle` (
  `id` int(11) NOT NULL,
  `agriculteur_id` int(11) NOT NULL,
  `nom` varchar(150) DEFAULT NULL,
  `superficie` decimal(10,2) DEFAULT NULL,
  `type_terre` enum('ARGILEUSE','SABLEUSE','LIMONEUSE','CALCAIRE','HUMIFERE','SALINE','MIXTE','AUTRE') DEFAULT 'AUTRE',
  `localisation` varchar(150) DEFAULT NULL,
  `date_creation` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `plans_irrigation`
--

CREATE TABLE `plans_irrigation` (
  `plan_id` int(11) NOT NULL,
  `id_culture` int(11) DEFAULT NULL,
  `nom_culture` varchar(100) DEFAULT NULL,
  `date_demande` datetime DEFAULT current_timestamp(),
  `statut` varchar(50) DEFAULT 'en_attente',
  `volume_eau_propose` float DEFAULT NULL,
  `temp_irrigation` time DEFAULT NULL,
  `temp` datetime DEFAULT NULL,
  `donnees_meteo_json` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `plans_irrigation`
--

INSERT INTO `plans_irrigation` (`plan_id`, `id_culture`, `nom_culture`, `date_demande`, `statut`, `volume_eau_propose`, `temp_irrigation`, `temp`, `donnees_meteo_json`) VALUES
(1, NULL, NULL, '2026-02-14 17:46:32', 'brouillon', 35, '00:00:00', '2026-02-14 17:46:32', NULL),
(2, NULL, 'Tomates Test', '2026-02-14 17:49:31', 'en_attente', 45.5, '06:30:00', '2026-02-14 17:49:31', '{\"temperature\": 28, \"humidite\": 65}');

-- --------------------------------------------------------

--
-- Table structure for table `plans_irrigation_jour`
--

CREATE TABLE `plans_irrigation_jour` (
  `id` int(11) NOT NULL,
  `plan_id` int(11) NOT NULL,
  `jour` varchar(10) NOT NULL,
  `eau_mm` float DEFAULT 0,
  `temps_min` int(11) DEFAULT 0,
  `temp_c` float DEFAULT 0,
  `semaine_debut` date NOT NULL DEFAULT '2024-01-01'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `plans_irrigation_jour`
--

INSERT INTO `plans_irrigation_jour` (`id`, `plan_id`, `jour`, `eau_mm`, `temps_min`, `temp_c`, `semaine_debut`) VALUES
(99, 1, 'MON', 4.72, 24, 28.7, '2026-02-09'),
(100, 1, 'TUE', 5.04, 26, 21.3, '2026-02-09'),
(101, 1, 'WED', 4.75, 24, 21.9, '2026-02-09'),
(102, 1, 'THU', 5.1, 26, 29.2, '2026-02-09'),
(103, 1, 'FRI', 5.83, 30, 28.6, '2026-02-09'),
(104, 1, 'SAT', 4.78, 24, 28.7, '2026-02-09'),
(105, 1, 'SUN', 4.78, 24, 20.1, '2026-02-09'),
(106, 1, 'MON', 5.26, 27, 21.1, '2026-02-16'),
(107, 1, 'TUE', 4.39, 22, 30.2, '2026-02-16'),
(108, 1, 'WED', 5.17, 26, 24.6, '2026-02-16'),
(109, 1, 'THU', 5.29, 27, 20.6, '2026-02-16'),
(110, 1, 'FRI', 4.4, 22, 24.4, '2026-02-16'),
(111, 1, 'SAT', 4.82, 25, 25.7, '2026-02-16'),
(112, 1, 'SUN', 5.67, 29, 30.6, '2026-02-16');

-- --------------------------------------------------------

--
-- Table structure for table `reclamations`
--

CREATE TABLE `reclamations` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `date_creation` datetime NOT NULL DEFAULT current_timestamp(),
  `statut` varchar(30) NOT NULL DEFAULT 'EN_ATTENTE',
  `reponse` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reclamations`
--

INSERT INTO `reclamations` (`id`, `utilisateur_id`, `categorie`, `titre`, `description`, `date_creation`, `statut`, `reponse`) VALUES
(6, 36, 'AUTRE', 'reclamation Sami', 'Voici une réclamation pur le Sami', '2026-02-13 14:36:12', 'EN_ATTENTE', 'oussama Fattoumi (EXPERT) : la réponse est longue donc elle doit être affiché\nMaatoug Ayoub (ADMIN) : aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\nMaatoug Ayoub (ADMIN) : jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj\nMaatoug Ayoub (ADMIN) : ppppppppppppppppppppppppppppppppppppppppppppppppppppppPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP\nMaatoug Ayoub (ADMIN) : PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP\nMaatoug Ayoub (ADMIN) : PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP\nMaatoug Ayoub (ADMIN) : PPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO'),
(7, 38, 'SERVICE', 'service', 'bad services', '2026-02-13 15:22:45', 'EN_ATTENTE', 'sami maatoug (AGRICULTEUR) : courte réponse');

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `annonce_id` int(11) NOT NULL,
  `demandeur_id` int(11) NOT NULL,
  `proprietaire_id` int(11) NOT NULL,
  `date_debut` date NOT NULL,
  `date_fin` date NOT NULL,
  `quantite` int(11) DEFAULT 1,
  `prix_total` decimal(10,2) NOT NULL,
  `caution` decimal(10,2) DEFAULT 0.00,
  `statut` enum('EN_ATTENTE','ACCEPTEE','REFUSEE','EN_COURS','TERMINEE','ANNULEE') DEFAULT 'EN_ATTENTE',
  `message_demande` text DEFAULT NULL,
  `reponse_proprietaire` text DEFAULT NULL,
  `date_demande` timestamp NOT NULL DEFAULT current_timestamp(),
  `date_reponse` timestamp NULL DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `contrat_url` varchar(500) DEFAULT NULL,
  `contrat_signe` tinyint(1) DEFAULT 0,
  `date_signature_contrat` timestamp NULL DEFAULT NULL,
  `paiement_effectue` tinyint(1) DEFAULT 0,
  `date_paiement` timestamp NULL DEFAULT NULL,
  `mode_paiement` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `cin` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motDePasse` varchar(255) NOT NULL,
  `role` varchar(40) NOT NULL,
  `dateCreation` date NOT NULL,
  `signature` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`) VALUES
(35, 'oussama', 'Fattoumi', 88554411, 'oussama@gmail.com', 'oussama1', 'EXPERT', '2026-02-10', 'uploadssignatures1770758047802_Logo_ESPRIT_Ariana.jpg'),
(36, 'Sami', 'Maatoug', 2030501, 'sami@gmail.com', 'sami*11', 'AGRICULTEUR', '2026-02-10', 'uploads/signatures/1770850459714_TuNour.jpg'),
(37, 'Ayoub22', 'Maatoug22', 11429920, 'maatougayoub7@gmail.com', 'pwayoub', 'EXPERT', '2026-02-11', 'uploadssignatures1770843103211_user.png'),
(38, 'Maatoug', 'Ayoub', 585, 'ayoub.maatoug@esprit.tn', 'pwayoub', 'ADMIN', '2026-02-13', 'uploads/signatures/1771084885464_Gemini_Generated_Image_mq403hmq403hmq40.png'),
(39, 'Jerbi', 'Amenallah', 12345678, 'amenallah@agriflow.tn', 'amenallah1', 'AGRICULTEUR', '2026-02-10', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `agriculteurs`
--
ALTER TABLE `agriculteurs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `annonces`
--
ALTER TABLE `annonces`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_categorie` (`categorie`),
  ADD KEY `idx_prix` (`prix`),
  ADD KEY `idx_proprietaire` (`proprietaire_id`);

--
-- Indexes for table `annonce_photos`
--
ALTER TABLE `annonce_photos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_annonce` (`annonce_id`);

--
-- Indexes for table `collab_applications`
--
ALTER TABLE `collab_applications`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_application` (`request_id`,`candidate_id`),
  ADD KEY `idx_request` (`request_id`),
  ADD KEY `idx_candidate` (`candidate_id`);

--
-- Indexes for table `collab_requests`
--
ALTER TABLE `collab_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `requester_id` (`requester_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_dates` (`start_date`,`end_date`);

--
-- Indexes for table `cultures`
--
ALTER TABLE `cultures`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_parcelle` (`parcelle_id`),
  ADD KEY `idx_proprietaire` (`proprietaire_id`);

--
-- Indexes for table `culture_vendue`
--
ALTER TABLE `culture_vendue`
  ADD PRIMARY KEY (`id_vente`),
  ADD KEY `idx_culture` (`id_culture`),
  ADD KEY `idx_acheteur` (`id_acheteur`);

--
-- Indexes for table `parcelle`
--
ALTER TABLE `parcelle`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_agriculteur` (`agriculteur_id`);

--
-- Indexes for table `experts`
--
ALTER TABLE `experts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_message_annonce` (`annonce_id`),
  ADD KEY `fk_message_reservation` (`reservation_id`),
  ADD KEY `idx_expediteur` (`expediteur_id`),
  ADD KEY `idx_destinataire` (`destinataire_id`),
  ADD KEY `idx_lu` (`lu`);

--
-- Indexes for table `plans_irrigation`
--
ALTER TABLE `plans_irrigation`
  ADD PRIMARY KEY (`plan_id`),
  ADD KEY `id_culture` (`id_culture`);

--
-- Indexes for table `plans_irrigation_jour`
--
ALTER TABLE `plans_irrigation_jour`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_plan_jour_date` (`plan_id`,`jour`,`semaine_debut`);

--
-- Indexes for table `reclamations`
--
ALTER TABLE `reclamations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reclamations_utilisateur` (`utilisateur_id`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reservation_annonce` (`annonce_id`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_demandeur` (`demandeur_id`),
  ADD KEY `idx_proprietaire` (`proprietaire_id`);

--
-- Indexes for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cin` (`cin`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `annonces`
--
ALTER TABLE `annonces`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `annonce_photos`
--
ALTER TABLE `annonce_photos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `collab_applications`
--
ALTER TABLE `collab_applications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `collab_requests`
--
ALTER TABLE `collab_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `cultures`
--
ALTER TABLE `cultures`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `culture_vendue`
--
ALTER TABLE `culture_vendue`
  MODIFY `id_vente` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parcelle`
--
ALTER TABLE `parcelle`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `plans_irrigation`
--
ALTER TABLE `plans_irrigation`
  MODIFY `plan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `plans_irrigation_jour`
--
ALTER TABLE `plans_irrigation_jour`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=113;

--
-- AUTO_INCREMENT for table `reclamations`
--
ALTER TABLE `reclamations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `agriculteurs` — add Amenallah Jerbi
--
INSERT INTO `agriculteurs` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `carte_pro`, `adresse`, `parcelles`) VALUES
(39, 'Jerbi', 'Amenallah', 12345678, 'amenallah@agriflow.tn', 'amenallah1', 'AGRICULTEUR', '2026-02-10', '', NULL, 'Sousse', '');

--
-- Constraints for table `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `agriculteurs`
--
ALTER TABLE `agriculteurs`
  ADD CONSTRAINT `fk_agriculteurs_utilisateurs` FOREIGN KEY (`id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `annonces`
--
ALTER TABLE `annonces`
  ADD CONSTRAINT `fk_annonce_proprietaire` FOREIGN KEY (`proprietaire_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `annonce_photos`
--
ALTER TABLE `annonce_photos`
  ADD CONSTRAINT `fk_photo_annonce` FOREIGN KEY (`annonce_id`) REFERENCES `annonces` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `fk_reservation_annonce` FOREIGN KEY (`annonce_id`) REFERENCES `annonces` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reservation_demandeur` FOREIGN KEY (`demandeur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reservation_proprietaire` FOREIGN KEY (`proprietaire_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `fk_message_expediteur` FOREIGN KEY (`expediteur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_message_destinataire` FOREIGN KEY (`destinataire_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_message_annonce` FOREIGN KEY (`annonce_id`) REFERENCES `annonces` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_message_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `collab_applications`
--
ALTER TABLE `collab_applications`
  ADD CONSTRAINT `collab_applications_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `collab_requests` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `collab_applications_ibfk_2` FOREIGN KEY (`candidate_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `collab_requests`
--
ALTER TABLE `collab_requests`
  ADD CONSTRAINT `collab_requests_ibfk_1` FOREIGN KEY (`requester_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `experts`
--
ALTER TABLE `experts`
  ADD CONSTRAINT `fk_experts_utilisateurs` FOREIGN KEY (`id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `plans_irrigation`
--
ALTER TABLE `plans_irrigation`
  ADD CONSTRAINT `plans_irrigation_ibfk_1` FOREIGN KEY (`id_culture`) REFERENCES `cultures` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `plans_irrigation_jour`
--
ALTER TABLE `plans_irrigation_jour`
  ADD CONSTRAINT `plans_irrigation_jour_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `plans_irrigation` (`plan_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reclamations`
--
ALTER TABLE `reclamations`
  ADD CONSTRAINT `fk_reclamations_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
