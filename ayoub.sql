-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 14, 2026 at 09:13 PM
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
-- Database: `db-gusers`
--

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
(38, 'Maatoug', 'Ayoub', 585, 'ayoub.maatoug@esprit.tn', 'pwayoub', 'ADMIN', '2026-02-13', 'uploads/signatures/1771084885464_Gemini_Generated_Image_mq403hmq403hmq40.png');

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
-- Indexes for table `experts`
--
ALTER TABLE `experts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reclamations`
--
ALTER TABLE `reclamations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reclamations_utilisateur` (`utilisateur_id`);

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
-- AUTO_INCREMENT for table `reclamations`
--
ALTER TABLE `reclamations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- Constraints for dumped tables
--

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
-- Constraints for table `experts`
--
ALTER TABLE `experts`
  ADD CONSTRAINT `fk_experts_utilisateurs` FOREIGN KEY (`id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reclamations`
--
ALTER TABLE `reclamations`
  ADD CONSTRAINT `fk_reclamations_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
