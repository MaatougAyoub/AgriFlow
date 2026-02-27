-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 27, 2026 at 05:29 PM
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
-- Database: `agriflow8`
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
(19, 'Tracteur John Deere 6120M', 'Tracteur puissant 120 CV, parfait pour les grandes exploitations. Entretien regulier, pneus neufs. Disponible avec ou sans operateur.', 'LOCATION', 'DISPONIBLE', 250.00, 'jour', 'Tracteur', NULL, NULL, NULL, 'Sousse', NULL, NULL, 39, '2026-01-01', '2026-12-31', '2026-02-23 12:10:34', '2026-02-25 15:57:30', 1, 0, 500.00, NULL, 0, 'piece'),
(20, 'Engrais NPK 15-15-15 Premium', 'Engrais equilibre haute qualite pour toutes cultures. Sacs de 50kg, livraison possible sur Sousse et environs.', 'VENTE', 'DISPONIBLE', 45.00, 'sac', 'Engrais', NULL, NULL, NULL, 'Sousse', NULL, NULL, 39, '2026-01-01', '2026-12-31', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 0.00, NULL, 100, 'sac'),
(21, 'Systeme Irrigation Goutte a Goutte', 'Kit complet irrigation goutte a goutte pour 1 hectare. Tuyaux, goutteurs, filtre et programmateur inclus.', 'VENTE', 'DISPONIBLE', 1200.00, 'unite', 'Irrigation', NULL, NULL, NULL, 'Sousse', NULL, NULL, 39, '2026-01-01', '2026-12-31', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 0.00, NULL, 5, 'unite'),
(22, 'Moissonneuse-Batteuse Claas Lexion', 'Moissonneuse-batteuse professionnelle, ideale pour ble et orge. Capacite tremie 9000L.', 'LOCATION', 'DISPONIBLE', 800.00, 'jour', 'Moissonneuse', NULL, NULL, NULL, 'Tunis', NULL, NULL, 40, '2026-03-01', '2026-09-30', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 1, 0, 1000.00, NULL, 2, 'piece'),
(23, 'Semences Ble Dur Karim', 'Semences certifiees de ble dur variete Karim, adaptees au climat tunisien. Rendement eleve.', 'VENTE', 'DISPONIBLE', 35.00, 'sac', 'Semences', NULL, NULL, NULL, 'Tunis', NULL, NULL, 40, '2026-01-01', '2026-06-30', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 0.00, NULL, 200, 'sac'),
(24, 'Pulverisateur Agricole 600L', 'Pulverisateur traine 600 litres avec rampe 12m. Parfait pour traitement phytosanitaire.', 'LOCATION', 'DISPONIBLE', 120.00, 'jour', 'Outil', NULL, NULL, NULL, 'Tunis', NULL, NULL, 40, '2026-01-01', '2026-12-31', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 200.00, NULL, 1, 'piece'),
(25, 'Olives Chemlali Bio - Recolte 2025', 'Olives fraiches variete Chemlali, agriculture biologique. Ideales pour huile ou conserve.', 'VENTE', 'DISPONIBLE', 8.00, 'kg', 'Fruits', NULL, NULL, NULL, 'Sfax', NULL, NULL, 44, '2026-01-01', '2026-03-31', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 0.00, NULL, 500, 'kg'),
(26, 'Charrue Reversible 3 Socs', 'Charrue reversible 3 socs pour labour profond. Compatible tracteurs 80-120 CV.', 'LOCATION', 'DISPONIBLE', 80.00, 'jour', 'Outil', NULL, NULL, NULL, 'Sfax', NULL, NULL, 44, '2026-01-01', '2026-12-31', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 100.00, NULL, 1, 'piece'),
(27, 'Dattes Deglet Nour Premium', 'Dattes Deglet Nour premiere qualite, recolte manuelle. Calibre AAA, caisses de 5kg.', 'VENTE', 'DISPONIBLE', 25.00, 'kg', 'Fruits', NULL, NULL, NULL, 'Tozeur', NULL, NULL, 44, '2026-01-01', '2026-06-30', '2026-02-23 12:10:34', '2026-02-23 12:10:34', 0, 0, 0.00, NULL, 300, 'kg');

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
(10, 19, 'https://images.unsplash.com/photo-1530267981375-f0de937f5f13?w=400', 0),
(11, 20, 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400', 0),
(12, 21, 'https://images.unsplash.com/photo-1563514227147-6d2ff665a6a0?w=400', 0),
(13, 22, 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400', 0),
(14, 23, 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=400', 0),
(15, 24, 'https://images.unsplash.com/photo-1592982537447-6f2a6a0c7c10?w=400', 0),
(16, 25, 'https://images.unsplash.com/photo-1445282768818-728615cc910a?w=400', 0),
(17, 26, 'https://images.unsplash.com/photo-1500595046743-cd271d694d30?w=400', 0),
(18, 27, 'https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=400', 0);

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
  `applied_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_applications`
--

INSERT INTO `collab_applications` (`id`, `request_id`, `candidate_id`, `full_name`, `phone`, `email`, `years_of_experience`, `motivation`, `expected_salary`, `status`, `applied_at`, `updated_at`) VALUES
(3, 2, 3, 'Mohamed Slimani', '98765432', 'mohamed@example.com', 2, 'Je cherche à apprendre et je suis très sérieux dans mon travail.', 35.00, 'APPROVED', '2026-02-20 00:02:07', '2026-02-21 03:23:17'),
(4, 5, 1, 'yakine', '000', 'yy@yy.com', 6, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 20.00, 'APPROVED', '2026-02-20 00:05:32', '2026-02-20 02:24:04'),
(5, 9, 1, 'aa', '0000000', 'bb@bb.com', 5, 'je suis aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 50.00, 'APPROVED', '2026-02-22 14:39:53', '2026-02-22 14:42:28'),
(8, 8, 1, 'bbbbbbbbbbbbb', '1111111111111', 'bb@bb.bb', 5, 'peut etre  wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww', 100.00, 'PENDING', '2026-02-22 15:03:13', '2026-02-22 15:03:13'),
(9, 6, 1, 'ccc', '00000000', 'aaa@aaa.c', 0, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 10.00, 'PENDING', '2026-02-22 15:24:14', '2026-02-22 15:24:14'),
(11, 11, 1, 'ee', '0000000', 'cccc@cc.cc', 5, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 44.00, 'PENDING', '2026-02-23 02:37:20', '2026-02-23 02:37:20'),
(13, 10, 1, 'aaa', '00000000', 'zzz@zz.v', 5, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 50.00, 'APPROVED', '2026-02-23 09:25:18', '2026-02-23 09:28:23'),
(14, 12, 1, 'yakine', '28121078', 'yakkine@iii.fr', 5, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 50.00, 'REJECTED', '2026-02-26 03:10:37', '2026-02-26 03:12:23'),
(15, 15, 2, 'aymen gh', '00000000', 'yaki@fff.com', 5, 'qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq', 50.00, 'PENDING', '2026-02-27 03:26:29', '2026-02-28 03:26:29'),
(70, 2, 1, 'aymen ghabi', '00000000', 'ayme,@yy.vom', 8, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 50.00, 'APPROVED', '2026-02-28 03:29:50', '2026-02-26 03:41:04'),
(71, 2, 71, 'ahmed garci', '11111111', 'ahmed@gmail.com', 0, 'je suis motivé , je suis passionné eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee', 90.00, 'PENDING', '2026-02-27 03:31:56', '2026-02-26 03:31:56'),
(74, 2, 74, 'imen ben kilani', '28121457', 'imen@imen.cpm', 10, 'motivé motivé  motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé motivé ', 10.00, 'PENDING', '2026-02-27 03:38:15', '2026-02-27 03:38:15'),
(75, 14, 1, 'ayoub', '111111111', 'ayy@yy.com', 5, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 20.00, 'APPROVED', '2026-02-26 23:09:29', '2026-02-26 23:10:03'),
(76, 15, 1, 'Ayoub', '20305177', 'ayoub.maatoug@ipeib.ucar.tn', 6, 'lettre de motivation\niiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii', 30.00, 'PENDING', '2026-02-27 16:03:40', '2026-02-27 16:03:40'),
(77, 16, 1, 'sami', '20305177', 'maatougsami25@gmail.com', 10, 'yesssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss', 50.00, 'PENDING', '2026-02-27 16:09:28', '2026-02-27 16:09:28');

-- --------------------------------------------------------

--
-- Table structure for table `collab_requests`
--

CREATE TABLE `collab_requests` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `location` varchar(100) NOT NULL,
  `latitude` decimal(10,7) DEFAULT NULL,
  `longitude` decimal(10,7) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `needed_people` int(11) NOT NULL DEFAULT 1,
  `salary` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` varchar(50) NOT NULL DEFAULT 'PENDING',
  `requester_id` bigint(20) NOT NULL DEFAULT 1,
  `publisher` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `collab_requests`
--

INSERT INTO `collab_requests` (`id`, `title`, `description`, `location`, `latitude`, `longitude`, `start_date`, `end_date`, `needed_people`, `salary`, `status`, `requester_id`, `publisher`, `created_at`, `updated_at`) VALUES
(2, 'Plantation de tomates', 'Aide nécessaire pour la plantation de tomates dans une grande serre. Débutants acceptés.', 'Tunis', NULL, NULL, '2026-03-01', '2026-03-05', 3, 35.00, 'APPROVED', 1, NULL, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(5, 'recolte des tomates ', 'Besoin dun agriculteur serieux avec experience ', 'manzel abderahmen', NULL, NULL, '2026-02-21', '2026-03-01', 1, 20.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-19 23:42:19', '2026-02-19 23:54:28'),
(6, 'aaaaa', 'a', 'aa', NULL, NULL, '2026-02-22', '2026-02-24', 1, 55.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-20 00:34:57', '2026-02-20 00:35:28'),
(7, 'Plantation des pommes de terres', 'On cherche 4 ouvriers serieux', 'Sfax', NULL, NULL, '2026-02-28', '2026-03-08', 4, 50.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-21 02:45:28', '2026-02-21 02:46:00'),
(8, 'Récolte oranges ', 'on recherche ouvrier serieux', 'Bizerte', NULL, NULL, '2026-03-06', '2026-03-08', 1, 70.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-21 03:29:36', '2026-02-21 03:31:09'),
(9, 'feunouilles', 'besoin dagriculteurs', 'Bizerte', NULL, NULL, '2026-02-24', '2026-03-01', 1, 40.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-22 14:35:10', '2026-02-22 14:36:22'),
(11, 'recolte fraise', 'recherche', 'Bizerte', NULL, NULL, '2026-02-24', '2026-02-25', 1, 55.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-23 02:36:14', '2026-02-23 02:36:42'),
(12, 'Olives recolte', 'recherche ', 'Azmour, Délégation Kélibia, Gouvernorat Nabeul, 8055, Tunisie', 36.9279390, 11.0137939, '2026-02-27', '2026-03-01', 1, 50.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-26 01:59:52', '2026-02-26 02:00:14'),
(13, 'Plantation des Fraises', 'on cherche plantation fraise ', 'Sidi Ahmed, Délégation Bizerte Sud, Gouvernorat Bizerte, Tunisie', 37.2740528, 9.7229004, '2026-02-27', '2026-03-01', 1, 10.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-26 03:20:11', '2026-02-26 03:20:29'),
(14, 'Recolte des pommes ', 'on cherche des personnes seriesux pou......', 'Cherichira, Délégation Essouassi, Gouvernorat Mahdia, Tunisie', 35.2994355, 10.5249023, '2026-02-28', '2026-03-03', 2, 53.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-26 23:06:37', '2026-02-26 23:07:56'),
(15, 'recolte oranges', 'on cherche', 'Habib Thameur, Délégation El Hamma, Gouvernorat Gabès, Tunisie', 33.8521697, 9.6679688, '2026-02-28', '2026-03-01', 1, 44.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-26 23:31:18', '2026-02-26 23:31:41'),
(16, 'Récolte tomate ', 'illi yji ', 'Sidi Harreth, Délégation Kasserine Sud, Gouvernorat Kasserine, Tunisie', 35.2456191, 8.8165283, '2026-02-28', '2026-03-05', 2, 440.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-27 16:04:09', '2026-02-27 16:05:57');

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
-- Table structure for table `diagnosti`
--

CREATE TABLE `diagnosti` (
  `id_diagnostic` int(11) NOT NULL,
  `id_agriculteur` int(11) NOT NULL,
  `nom_culture` varchar(100) NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `description` text NOT NULL,
  `reponse_expert` text DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'En attente',
  `date_envoi` datetime DEFAULT NULL,
  `date_reponse` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `diagnosti`
--

INSERT INTO `diagnosti` (`id_diagnostic`, `id_agriculteur`, `nom_culture`, `image_path`, `description`, `reponse_expert`, `statut`, `date_envoi`, `date_reponse`) VALUES
(20, 1, 'Pommes', 'C:\\Users\\wess\\Pictures\\Screenshots\\Capture d\'écran 2026-02-18 204010.png', 'L\'image présente deux pommes accrochées à un arbre. Les pommes ont une coloration irrégulière, avec des zones rouges et jaunes. Des taches sombres et des zones décolorées sont visibles sur leur surface.\n\nCes symptômes pourraient être causés par plusieurs facteurs, notamment des maladies ou des dommages physiologiques. \n\nUne maladie courante qui affecte les pommes et cause de telles altérations est la **rouille du pommier (Gymnosporangium sabinae)*\nSi les taches sont petites et brunes avec un centre clair, cela pourrait indiquer une infection par la tavelure du pommier. \n\nUn traitement possible contre ces maladies fongiques consiste en l\'application de fongicides à base de substances actives telles que la chlorothalonil ou la mancozèbe. \n\nIl est important de noter qu\'un diagnostic précis nécessite souvent un examen plus approfondi et des tests. Il est conseillé de consulter un expert en phytopathologie ou en agronomie pour une identification précise de la cause et des mesures appropriées. Des mesures préventives, comme l\'élimination des feuilles infectées et l\'amélioration de la circulation de l\'air autour des arbres, peuvent aussi être bénéfiques.', '\n--- PRODUIT RECOMMANDÉ ---\nNom : Gommex-C\nDosage : 3 L / hectare\nFréquence : 2 fois par mois\nNote : Ne pas appliquer sur un arbre affaibli. Nettoyer le matériel après usage. Éviter le contact avec la peau.\n---------------------------\n', 'Valide', '2026-02-21 21:38:19', NULL),
(21, 1, 'Citrons', 'C:\\Users\\wess\\Pictures\\Screenshots\\Capture d\'écran 2026-02-19 210421.png', 'L\'image représente des fruits jaunes, probablement des mandarines ou des oranges, présentant des taches vertes et sombres, ce qui suggère la présence de symptômes de maladie.\n\n**État de la culture ou du fruit :**\n\nLes fruits sont globalement bien développés mais arborent des taches sombres irrégulières. L\'état général de la plante n\'est pas complètement visible sur l\'image, mais les feuilles paraissent saines.\n\n**Maladies identifiées :**\n\nSur la base des symptômes visibles (taches vertes et sombres sur les fruits), il est probable que la maladie en question soit la **\"Green Spot\"** ou plus communément appelée en français **\"Tache verte\"** ou encore **\"Marmorature\"** causée par des bactéries comme *Pseudomonas syringae* ou des conditions physiologiques. Cependant, la maladie la plus probable étant donné la description pourrait être la **\"Tache verte à Citrus canker\"** causée par *Xanthomonas citri*.\n\n', NULL, 'En attente', '2026-02-21 21:42:15', NULL),
(23, 1, 'Citrons', 'C:\\Users\\wess\\Pictures\\Screenshots\\Capture d\'écran 2026-02-21 165706.png', 'Objet : Demande de réclamation agricole - Tâches brunes sur citron\n\nMadame, Monsieur,\n\nJe vous écris pour signaler un problème sur une culture de citronniers dans mon exploitation agricole. Les citronniers présentent des tâches brunes sur les fruits (voir photo ci-jointe).\n\nLe problème observé :\n\nLes tâches brunes sur les citrons sont un symptôme de maladie, probablement causée par une infection fongique. Ces tâches peuvent entraîner une pourriture des fruits.\n\nDommages possibles :\n\n* Perte de qualité et de quantité de la production\n* Risque de contamination d\'autres plantes\n* Coûts supplémentaires pour le traitement et la gestion de la maladie\n\nDemande d\'intervention :\n\nJe vous demande de vous déplacer sur mon exploitation pour examiner les citronniers et déterminer la cause exacte du problème. Je souhaiterais également bénéficier de vos conseils pour mettre en place un plan d\'action afin de traiter la maladie.\n\nJe vous remercie d\'avance pour votre attention à cette affaire et je reste à votre disposition pour tout renseignement complémentaire.\n\nCordialement,\n[Votre nom]', NULL, 'En attente', '2026-02-22 21:50:21', NULL);

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
(4, 1, NULL, '2026-02-15 19:13:35', 'brouillon', 35, '00:00:00', '2026-02-15 19:13:35', NULL),
(5, 3, NULL, '2026-02-15 19:13:48', 'brouillon', 50, '00:00:00', '2026-02-15 19:13:48', NULL),
(7, 9, NULL, '2026-02-15 22:33:20', 'brouillon', 60, '00:00:00', '2026-02-15 22:33:20', NULL),
(8, 2, NULL, '2026-02-16 11:00:04', 'brouillon', 40, '00:00:00', '2026-02-16 11:00:04', NULL),
(9, 4, NULL, '2026-02-18 09:46:53', 'brouillon', 35, '00:00:00', '2026-02-18 09:46:53', NULL),
(10, 8, NULL, '2026-02-18 09:47:00', 'brouillon', 70, '00:00:00', '2026-02-18 09:47:00', NULL),
(11, 11, NULL, '2026-02-18 20:38:52', 'brouillon', 45, '00:00:00', '2026-02-18 20:38:52', NULL),
(12, 5, NULL, '2026-02-19 19:01:10', 'brouillon', 45, '00:00:00', '2026-02-19 19:01:10', NULL),
(13, 3, NULL, '2026-02-20 11:32:16', 'brouillon', 50, '00:00:00', '2026-02-20 11:32:16', NULL),
(14, 1, NULL, '2026-02-20 11:32:25', 'brouillon', 35, '00:00:00', '2026-02-20 11:32:25', NULL),
(15, 1, NULL, '2026-02-20 11:52:18', 'brouillon', 35, '00:00:00', '2026-02-20 11:52:18', NULL);

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
  `semaine_debut` date NOT NULL DEFAULT '2024-01-01',
  `humidite` float DEFAULT 0,
  `pluie` float DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `plans_irrigation_jour`
--

INSERT INTO `plans_irrigation_jour` (`id`, `plan_id`, `jour`, `eau_mm`, `temps_min`, `temp_c`, `semaine_debut`, `humidite`, `pluie`) VALUES
(498, 15, 'THU', 4.2, 21, 13.5, '2026-02-16', 97, 0.3),
(499, 15, 'TUE', 0.7, 4, 11, '2026-02-16', 95, 3.8),
(500, 15, 'WED', 4.5, 23, 11.3, '2026-02-16', 95, 0),
(501, 15, 'SAT', 1.2, 6, 10.3, '2026-02-16', 93, 3.3),
(502, 15, 'FRI', 1.5, 8, 10.9, '2026-02-16', 91, 3),
(503, 15, 'MON', 4.5, 23, 11.9, '2026-02-16', 92, 0),
(504, 15, 'SUN', 0.0999999, 1, 6.6, '2026-02-16', 92, 4.4),
(512, 13, 'THU', 6.42857, 33, 13.9, '2026-02-16', 96, 0),
(513, 13, 'TUE', 4.62857, 24, 10.8, '2026-02-16', 95, 1.8),
(514, 13, 'WED', 6.42857, 33, 11.9, '2026-02-16', 96, 0),
(515, 13, 'SAT', 2.12857, 11, 10.3, '2026-02-16', 95, 4.3),
(516, 13, 'FRI', 6.22857, 32, 11.6, '2026-02-16', 94, 0.2),
(517, 13, 'MON', 6.42857, 33, 11.6, '2026-02-16', 92, 0),
(518, 13, 'SUN', 2.02857, 11, 6.6, '2026-02-16', 92, 4.4),
(519, 10, 'THU', 8.7, 44, 13.5, '2026-02-16', 97, 0.3),
(520, 10, 'TUE', 5.2, 26, 11, '2026-02-16', 95, 3.8),
(521, 10, 'WED', 9, 45, 11.3, '2026-02-16', 95, 0),
(522, 10, 'SAT', 5.7, 29, 10.3, '2026-02-16', 93, 3.3),
(523, 10, 'FRI', 6, 30, 10.9, '2026-02-16', 91, 3),
(524, 10, 'MON', 9, 45, 11.9, '2026-02-16', 92, 0),
(525, 10, 'SUN', 4.6, 23, 6.6, '2026-02-16', 92, 4.4),
(533, 10, 'MON', 9, 45, 11.9, '2026-02-23', 92, 0),
(534, 10, 'TUE', 5.2, 26, 11, '2026-02-23', 95, 3.8),
(535, 10, 'WED', 9, 45, 11.3, '2026-02-23', 95, 0),
(536, 10, 'THU', 8.7, 44, 13.5, '2026-02-23', 97, 0.3),
(537, 10, 'FRI', 6, 30, 10.9, '2026-02-23', 91, 3),
(538, 10, 'SAT', 5.7, 29, 10.3, '2026-02-23', 93, 3.3),
(539, 10, 'SUN', 4.6, 23, 6.6, '2026-02-23', 92, 4.4),
(561, 8, 'THU', 5.71429, 29, 25.5, '2026-02-16', 30, 0),
(562, 8, 'TUE', 5.71429, 29, 29.1, '2026-02-16', 42, 0),
(563, 8, 'WED', 5.71429, 29, 28, '2026-02-16', 60, 0),
(564, 8, 'SAT', 5.71429, 29, 26.4, '2026-02-16', 52, 0),
(565, 8, 'FRI', 5.71429, 29, 28.6, '2026-02-16', 41, 0),
(566, 8, 'MON', 5.71429, 29, 28.4, '2026-02-16', 26, 0),
(567, 8, 'SUN', 5.71429, 29, 25.5, '2026-02-16', 56, 0);

-- --------------------------------------------------------

--
-- Table structure for table `produits_phytosanitaires`
--

CREATE TABLE `produits_phytosanitaires` (
  `id_produit` int(11) NOT NULL,
  `nom_produit` varchar(100) NOT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `frequence_application` varchar(100) DEFAULT NULL,
  `remarques` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produits_phytosanitaires`
--

INSERT INTO `produits_phytosanitaires` (`id_produit`, `nom_produit`, `dosage`, `frequence_application`, `remarques`) VALUES
(13, 'Fongicide Tavelure-Plus', '2 L / hectare', '1 fois tous les 10 jours', 'Appliquer par temps sec et sans vent. Porter des gants et un masque de protection. Ne pas traiter pendant la floraison.'),
(16, 'Anti-Rot Grenade', '2.5 L / hectare', '2 fois par mois', 'Appliquer tôt le matin. Ne pas mélanger avec d’autres produits chimiques. Conserver hors de portée des enfants.'),
(17, 'Bio-Protect', '4 L / hectare', '1 fois tous les 15 jours', 'Produit biologique sans danger pour l’environnement. Bien agiter avant utilisation. Stocker à l’abri de la chaleur.'),
(18, 'Scab-Control', '2 L / hectare', '1 fois tous les 7 jours', 'Appliquer avant l’apparition des symptômes. Porter des équipements de protection. Éviter les jours de pluie.'),
(19, 'Oidium-Fix', '1.8 L / hectare', '2 fois par mois', 'Ne pas dépasser la dose recommandée. Bien nettoyer le pulvérisateur après usage.'),
(20, 'F1-2203', '2L/hec', '1 fois /moins', 'Ne pas appliquer sur un arbre affaibli. Nettoyer le matériel après usage. Éviter le contact avec la peau.');

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
(6, 2, 39, 36, '2026-02-16', '2026-02-17', 1, 1760.00, 3000.00, 'EN_ATTENTE', 'aaaaaaaa', NULL, '2026-02-16 00:11:01', NULL, '2026-02-16 00:11:01', NULL, 0, NULL, 0, NULL, NULL),
(7, 19, 74, 39, '2026-02-25', '2026-02-26', 1, 550.00, 500.00, 'EN_ATTENTE', 'aaaaaaaa', NULL, '2026-02-25 15:55:14', NULL, '2026-02-25 15:55:14', NULL, 0, NULL, 0, NULL, NULL),
(8, 19, 74, 39, '2026-02-25', '2026-02-26', 1, 550.00, 500.00, 'ACCEPTEE', 'aaaaaaa', 'Demande acceptée. Bienvenue !', '2026-02-25 15:56:46', '2026-02-25 15:57:30', '2026-02-25 15:56:46', NULL, 0, NULL, 1, '2026-02-25 15:58:14', 'Carte bancaire (Stripe)');

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
(39, 'maatoug', 'ayoub', 11429920, 'ayoub.maatoug@esprit.tn', '$2a$12$aiBquFb/ffsalNnpdndEFuR0m0ZLh5luTEYjS.hVCfPS/plKY2j3q', 'ADMIN', '2026-02-16', 'C:\\xampp\\htdocs\\signatures\\1771633850595_signature_ayoub.jpg', 100.5, NULL, NULL, NULL, NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(43, 'Fattoumi', 'Oussama', 66554433, 'oussama@gmail.com', 'pwoussama', 'EXPERT', '2026-02-20', 'C:\\xampp\\htdocs\\signatures\\1771634526954_signature_oussama.jpg', NULL, NULL, NULL, NULL, 'C:\\xampp\\htdocs\\certifications\\1771634537416_diplome_expert_Oussama_.png', 'APPROVED', NULL, NULL, NULL, NULL),
(44, 'Baji', 'Badis', 99663388, 'badis@gmail.com', 'pwbadis', 'AGRICULTEUR', '2026-02-20', 'C:\\xampp\\htdocs\\signatures\\1771634629497_signature_badis.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771634642357_____________.jpg', 'araiana', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(49, 'sahli', 'yakine', 12345666, 'yakine@gmail.com', 'pwyakine', 'AGRICULTEUR', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771634702295_signature_yakine.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771634716151_logo.png', 'bizerte', '', NULL, 'APPROVED', NULL, NULL, NULL, NULL),
(50, 'Expert', 'Expert', 99999999, 'expert@gmail.com', 'pwexpert', 'EXPERT', '2026-02-21', 'C:\\xampp\\htdocs\\signatures\\1771632506007_signature_expert.jpg', NULL, NULL, NULL, NULL, 'C:\\xampp\\htdocs\\certifications\\1771632513711_certification_expert.jpg', 'APPROVED', NULL, NULL, NULL, NULL),
(63, 'SAMI', 'MAATOUG', 74100000, 'maatougsami25@gmail.com', '$2a$12$OCCHJP1GqPJz5uThamkB/...VSdgqZiUrcyF5u0Y8CwR7T6nR675y', 'AGRICULTEUR', '2026-02-22', 'C:\\xampp\\htdocs\\signatures\\1771730327340_signature_sami.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771730400439_sami____________.jpg', 'rue ali douagi - Ras Jebel', '', NULL, 'APPROVED', NULL, NULL, 'سامي', 'معتوق'),
(70, 'Jerbi', 'Amenallah', 12345678, 'amenallah@agriflow.tn', 'pwamen', 'AGRICULTEUR', '2026-02-22', 'C:\\xampp\\htdocs\\signatures\\1771779144715_signature_amen.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771779159143__amen_____________.jpg', 'korba', '', NULL, 'APPROVED', NULL, NULL, 'أمان الله', 'جربي'),
(74, 'Ayoub', 'Maatoug', 11223344, 'ayoub.maatoug@ipeib.ucar.tn', '$2a$12$VqNLs/i7EdWxtFUxa./kQOQkecF3kgoCM/8oDAKvPJgICvomwTLbe', 'AGRICULTEUR', '2026-02-23', 'C:\\xampp\\htdocs\\signatures\\1771821459799_signature_ayoub.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1771821529933_Carte_pro__ayoub_.png', 'eeeeee', '', NULL, 'APPROVED', NULL, NULL, 'أيوب', 'معتوق'),
(75, 'AYOUB', 'MAATOUG', 25042000, 'maatougayoub7@gmail.com', '$2a$12$aiBquFb/ffsalNnpdndEFuR0m0ZLh5luTEYjS.hVCfPS/plKY2j3q', 'AGRICULTEUR', '2026-02-25', 'C:\\xampp\\htdocs\\signatures\\1772034356349_signature_ayoub.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1772034408339_carte_pro_25042000.png', 'Ras Jebel 7070', '', NULL, 'APPROVED', NULL, NULL, 'أيوب', 'معتوق'),
(76, 'yakine', 'sahli', 77882020, 'yakinesahli48@gmail.com', '$2a$12$RscsfaflKeKNiwTGqruexeZL.DS8D6tmcxyFErpOeKUTIwNw8HX0m', 'AGRICULTEUR', '2026-02-27', 'C:\\xampp\\htdocs\\signatures\\1772209232788_signature_yakine.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1772209276977_Carte_pro_yakine.png', 'Bizerte', '', NULL, 'APPROVED', '', 1, 'يقين', 'ساحلي'),
(77, 'sahli', 'yakine eddine', 98765432, 'yakineddine.sahli@isgb.ucar.tn', '$2a$12$qD6nzyC34wBASVrNEkDXUOqV2hnzqppcBdiYlm4H7HuLiDNviraBm', 'AGRICULTEUR', '2026-02-27', 'C:\\xampp\\htdocs\\signatures\\1772209652466_signature_yakine.jpg', NULL, 'C:\\xampp\\htdocs\\cartes\\1772209684709_carte_pro_yakine22.png', 'ariana', '', NULL, 'APPROVED', '', 1, 'يقين', 'ساحلي');

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
-- Indexes for table `cultures`
--
ALTER TABLE `cultures`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `diagnosti`
--
ALTER TABLE `diagnosti`
  ADD PRIMARY KEY (`id_diagnostic`);

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
-- Indexes for table `parcelle`
--
ALTER TABLE `parcelle`
  ADD PRIMARY KEY (`id`);

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
-- Indexes for table `produits_phytosanitaires`
--
ALTER TABLE `produits_phytosanitaires`
  ADD PRIMARY KEY (`id_produit`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `annonce_photos`
--
ALTER TABLE `annonce_photos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `collab_applications`
--
ALTER TABLE `collab_applications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=78;

--
-- AUTO_INCREMENT for table `collab_requests`
--
ALTER TABLE `collab_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `cultures`
--
ALTER TABLE `cultures`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `diagnosti`
--
ALTER TABLE `diagnosti`
  MODIFY `id_diagnostic` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parcelle`
--
ALTER TABLE `parcelle`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `plans_irrigation`
--
ALTER TABLE `plans_irrigation`
  MODIFY `plan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `plans_irrigation_jour`
--
ALTER TABLE `plans_irrigation_jour`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=568;

--
-- AUTO_INCREMENT for table `produits_phytosanitaires`
--
ALTER TABLE `produits_phytosanitaires`
  MODIFY `id_produit` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `reclamations`
--
ALTER TABLE `reclamations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=78;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
