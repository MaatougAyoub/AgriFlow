-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : ven. 20 fév. 2026 à 03:57
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `agriflow`
--

-- --------------------------------------------------------

--
-- Structure de la table `collab_requests`
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
-- Déchargement des données de la table `collab_requests`
--

INSERT INTO `collab_requests` (`id`, `title`, `description`, `location`, `latitude`, `longitude`, `start_date`, `end_date`, `needed_people`, `salary`, `status`, `requester_id`, `publisher`, `created_at`, `updated_at`) VALUES
(2, 'Plantation de tomates', 'Aide nécessaire pour la plantation de tomates dans une grande serre. Débutants acceptés.', 'Tunis', NULL, NULL, '2026-03-01', '2026-03-05', 3, 35.00, 'APPROVED', 1, NULL, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(3, 'Taille de vignes', 'Recherche de personnes expérimentées pour la taille de vignes. Travail minutieux.', 'Nabeul', NULL, NULL, '2026-03-10', '2026-03-15', 4, 50.00, 'APPROVED', 1, NULL, '2026-02-19 16:54:29', '2026-02-19 23:02:18'),
(4, 'Irrigation et entretien', 'Besoin d\'aide pour l\'entretien des systèmes d\'irrigation. Formation fournie.', 'Sousse', NULL, NULL, '2026-03-20', '2026-03-25', 2, 30.00, 'APPROVED', 1, NULL, '2026-02-19 16:54:29', '2026-02-19 16:54:29'),
(5, 'recolte des tomates ', 'Besoin dun agriculteur serieux avec experience ', 'manzel abderahmen', NULL, NULL, '2026-02-21', '2026-03-01', 1, 20.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-19 23:42:19', '2026-02-19 23:54:28'),
(6, 'aaaaa', 'a', 'aa', NULL, NULL, '2026-02-22', '2026-02-24', 1, 55.00, 'APPROVED', 1, 'Ali Ben Ahmed', '2026-02-20 00:34:57', '2026-02-20 00:35:28');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `collab_requests`
--
ALTER TABLE `collab_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_location` (`location`),
  ADD KEY `idx_dates` (`start_date`,`end_date`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `collab_requests`
--
ALTER TABLE `collab_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
