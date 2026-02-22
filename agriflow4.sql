-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 22, 2026 at 03:25 AM
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
-- Database: `agriflow4`
--

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
(1, 'Tracteur John Deere 6120M', 'Tracteur puissant, entretien régulier', 'LOCATION', 'DISPONIBLE', 250.00, 'jour', 'Tracteur', 'John Deere', '6120M', 2020, 'Sousse', NULL, NULL, 36, '2026-02-01', '2026-06-30', '2026-02-14 21:10:59', '2026-02-15 22:42:52', 1, 0, 1000.00, NULL, 0, 'kg'),
(2, 'Moissonneuse New Holland CR9080', 'Moissonneuse dernière génération', 'LOCATION', 'DISPONIBLE', 800.00, 'jour', 'Moissonneuse', 'New Holland', 'CR9080', 2019, 'Sfax', NULL, NULL, 36, '2026-03-01', '2026-05-31', '2026-02-14 21:10:59', '2026-02-15 22:42:52', 1, 0, 3000.00, NULL, 0, 'kg'),
(3, 'Engrais NPK 20-20-20', '50 sacs de 50kg disponibles', 'VENTE', 'DISPONIBLE', 85.00, 'sac', 'Engrais', 'SIAPE', 'NPK 20-20-20', NULL, 'Tunis', NULL, NULL, 36, '2026-01-01', '2026-12-31', '2026-02-14 21:10:59', '2026-02-15 22:42:52', 0, 0, 0.00, NULL, 0, 'kg'),
(4, 'Drone', 'drone', 'VENTE', 'DISPONIBLE', 250.00, 'jour', 'Drones', NULL, NULL, 0, 'Tunisie', NULL, NULL, 39, NULL, NULL, '2026-02-15 22:07:25', '2026-02-15 22:07:25', 0, 0, 0.00, NULL, 0, 'kg');

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
(3, 3, 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=250&fit=crop', 0),
(4, 4, 'https://m.media-amazon.com/images/I/61dKEc095AL._AC_SL1500_.jpg', 0);

-- --------------------------------------------------------

--
-- Table structure for table `collab_applications`
--

CREATE TABLE `collab_applications` (
  `id` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `candidate_id` bigint(20) NOT NULL DEFAULT 1,
  `full_name` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `years_of_experience` int(11) NOT NULL DEFAULT 0,
  `motivation` text NOT NULL,
  `expected_salary` decimal(10,2) DEFAULT 0.00,
  `status` varchar(50) NOT NULL DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_applications`
--

INSERT INTO `collab_applications` (`id`, `request_id`, `candidate_id`, `full_name`, `phone`, `email`, `years_of_experience`, `motivation`, `expected_salary`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 'Ahmed Ben Ali', '20123456', 'ahmed@example.com', 5, 'Je suis très motivé et j\'ai 5 ans d\'expérience dans la récolte d\'olives.', 45.00, 'PENDING', '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(2, 1, 2, 'Fatma Trabelsi', '25987654', 'fatma@example.com', 3, 'J\'ai travaillé dans plusieurs fermes à Bizerte. Je suis disponible immédiatement.', 40.00, 'APPROVED', '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(3, 2, 3, 'Mohamed Slimani', '98765432', 'mohamed@example.com', 2, 'Je cherche à apprendre et je suis très sérieux dans mon travail.', 35.00, 'PENDING', '2026-02-19 16:54:29', '2026-02-19 16:54:29');

-- --------------------------------------------------------

--
-- Table structure for table `collab_requests`
--

CREATE TABLE `collab_requests` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `location` varchar(100) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `needed_people` int(11) NOT NULL DEFAULT 1,
  `salary_per_day` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` varchar(50) NOT NULL DEFAULT 'PENDING',
  `requester_id` bigint(20) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_requests`
--

INSERT INTO `collab_requests` (`id`, `title`, `description`, `location`, `start_date`, `end_date`, `needed_people`, `salary_per_day`, `status`, `requester_id`, `created_at`, `updated_at`) VALUES
(1, 'Récolte d\'olives', 'Je cherche deux agriculteurs sérieux pour la récolte d\'olives. Travail en équipe, expérience souhaitée.', 'Bizerte', '2026-02-25', '2026-02-28', 5, 40.00, 'APPROVED', 1, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(2, 'Plantation de tomates', 'Aide nécessaire pour la plantation de tomates dans une grande serre. Débutants acceptés.', 'Tunis', '2026-03-01', '2026-03-05', 3, 35.00, 'APPROVED', 1, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(3, 'Taille de vignes', 'Recherche de personnes expérimentées pour la taille de vignes. Travail minutieux.', 'Nabeul', '2026-03-10', '2026-03-15', 4, 50.00, 'PENDING', 1, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(4, 'Irrigation et entretien', 'Besoin d\'aide pour l\'entretien des systèmes d\'irrigation. Formation fournie.', 'Sousse', '2026-03-20', '2026-03-25', 2, 30.00, 'APPROVED', 1, '2026-02-19 16:54:29', '2026-02-19 16:54:29');

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
(1, 1, NULL, '2026-02-14 17:46:32', 'brouillon', 35, '00:00:00', '2026-02-14 17:46:32', NULL),
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
(9, 44, 'ACCESS', 'access rec', 'relamationaaaaa', '2026-02-20 14:16:01', 'EN_ATTENTE', 'maatoug ayoub (ADMIN) : noooo\nmaatoug ayoub (ADMIN) : yes'),
(10, 44, 'PAIMENT', 'aaaaaa', 'aaaaaaaaaa', '2026-02-20 14:16:35', 'EN_ATTENTE', 'maatoug ayoub (ADMIN) : yes'),
(12, 49, 'AUTRE', 'rrr', 'rrrrr', '2026-02-21 00:45:17', 'EN_ATTENTE', NULL);

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

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`id`, `annonce_id`, `demandeur_id`, `proprietaire_id`, `date_debut`, `date_fin`, `quantite`, `prix_total`, `caution`, `statut`, `message_demande`, `reponse_proprietaire`, `date_demande`, `date_reponse`, `date_creation`, `contrat_url`, `contrat_signe`, `date_signature_contrat`, `paiement_effectue`, `date_paiement`, `mode_paiement`) VALUES
(2, 4, 36, 39, '2026-02-15', '2026-02-16', 1, 275.00, 0.00, 'REFUSEE', 'hey', 'noob', '2026-02-15 22:52:58', '2026-02-15 23:04:37', '2026-02-15 22:52:58', NULL, 0, NULL, 0, NULL, NULL),
(3, 1, 39, 36, '2026-02-15', '2026-02-16', 1, 550.00, 1000.00, 'EN_ATTENTE', 'salut', NULL, '2026-02-15 23:06:47', NULL, '2026-02-15 23:06:47', NULL, 0, NULL, 0, NULL, NULL),
(4, 4, 41, 39, '2026-02-15', '2026-02-16', 1, 275.00, 0.00, 'EN_ATTENTE', 'interessé', NULL, '2026-02-15 23:15:01', NULL, '2026-02-15 23:15:01', NULL, 0, NULL, 0, NULL, NULL),
(5, 1, 39, 36, '2026-02-16', '2026-02-17', 1, 550.00, 1000.00, 'EN_ATTENTE', 'salut', NULL, '2026-02-16 00:06:34', NULL, '2026-02-16 00:06:34', NULL, 0, NULL, 0, NULL, NULL),
(6, 2, 39, 36, '2026-02-16', '2026-02-17', 1, 1760.00, 3000.00, 'EN_ATTENTE', 'aaaaaaaa', NULL, '2026-02-16 00:11:01', NULL, '2026-02-16 00:11:01', NULL, 0, NULL, 0, NULL, NULL);

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
  `signature` varchar(500) NOT NULL,
  `revenu` double DEFAULT NULL,
  `carte_pro` varchar(500) DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `parcelles` varchar(255) DEFAULT NULL,
  `certification` varchar(500) DEFAULT NULL,
  `verification_status` varchar(20) NOT NULL DEFAULT 'APPROVED',
  `verification_reason` varchar(500) DEFAULT NULL,
  `verification_score` double DEFAULT NULL,
  `nom_ar` varchar(255) DEFAULT NULL,
  `prenom_ar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `revenu`, `carte_pro`, `adresse`, `parcelles`, `certification`, `verification_status`, `verification_reason`, `verification_score`, `nom_ar`, `prenom_ar`) VALUES
(39, 'maatoug', 'ayoub', 11429920, 'ayoub.maatoug@esprit.tn', 'pwayoub', 'ADMIN', '2026-02-16', 'C:\\xampp\\htdocs\\signatures\\1771633850595_signature_ayoub.jpg', 100.5, NULL, NULL, NULL, NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(40, 'Jerbi', 'Amenallah', 12345678, 'amenallah@agriflow.tn', 'pwamen', 'AGRICULTEUR', '2026-02-10', 'C:\\xampp\\htdocs\\signatures\\1771634439728_signature_amen.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771634452580__amen_____________.jpg', 'Sousse', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(43, 'Fattoumi', 'Oussama', 66554433, 'oussama@gmail.com', 'pwoussama', 'EXPERT', '2026-02-20', 'C:\\xampp\\htdocs\\signatures\\1771634526954_signature_oussama.jpg', NULL, NULL, NULL, NULL, 'C:\\xampp\\htdocs\\certifications\\1771634537416_diplome_expert_Oussama_.png', 'APPROVED', NULL, NULL, NULL, NULL),
(44, 'Baji', 'Badis', 99663388, 'badis@gmail.com', 'pwbadis', 'AGRICULTEUR', '2026-02-20', 'C:\\xampp\\htdocs\\signatures\\1771634629497_signature_badis.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771634642357_____________.jpg', 'araiana', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(49, 'sahli', 'yakine', 12345666, 'yakine@gmail.com', 'pwyakine', 'AGRICULTEUR', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771634702295_signature_yakine.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771634716151_logo.png', 'bizerte', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(50, 'Expert', 'Expert', 99999999, 'expert@gmail.com', 'pwexpert', 'EXPERT', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771632506007_signature_expert.jpg', NULL, NULL, NULL, NULL, 'C:\\xampp\\htdocs\\certifications\\1771632513711_certification_expert.jpg', 'APPROVED', NULL, NULL, NULL, NULL),
(55, 'Maatoug', 'Sami', 1010101, 'maatougsami25@gmail.com', 'pwsami', 'AGRICULTEUR', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771681072033_signature_sami.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771681090450_____________.jpg', 'rasjebel', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(56, 'Maatoug2', 'Ayoub2', 78451200, 'maatougayoub7@gmail.com', 'pwayoub2', 'AGRICULTEUR', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771693117443_signature_ayoub.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771693147960_____________ayoub.JPG', 'Ras Jebel - Rue Ali Douagi', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL);

--
-- Indexes for dumped tables
--

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
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_candidate` (`candidate_id`);

--
-- Indexes for table `collab_requests`
--
ALTER TABLE `collab_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_location` (`location`),
  ADD KEY `idx_dates` (`start_date`,`end_date`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `annonce_photos`
--
ALTER TABLE `annonce_photos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `collab_applications`
--
ALTER TABLE `collab_applications`
  ADD CONSTRAINT `fk_application_request` FOREIGN KEY (`request_id`) REFERENCES `collab_requests` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
