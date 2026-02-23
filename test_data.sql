-- =============================================
-- AgriFlow Marketplace - Donnees de test propres
-- Pour la validation du projet
-- =============================================

-- 1. Nettoyer les anciennes donnees
DELETE FROM annonce_photos;
DELETE FROM reservations;
DELETE FROM annonces;

-- 2. Annonces pour Ayoub Maatoug (id=39)
INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, categorie, localisation, proprietaire_id, date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution, quantite_disponible, unite_quantite)
VALUES
('Tracteur John Deere 6120M', 'Tracteur puissant 120 CV, parfait pour les grandes exploitations. Entretien regulier, pneus neufs. Disponible avec ou sans operateur.', 'LOCATION', 'DISPONIBLE', 250.00, 'jour', 'Tracteur', 'Sousse', 39, '2026-01-01', '2026-12-31', 1, 500.00, 3, 'piece'),
('Engrais NPK 15-15-15 Premium', 'Engrais equilibre haute qualite pour toutes cultures. Sacs de 50kg, livraison possible sur Sousse et environs.', 'VENTE', 'DISPONIBLE', 45.00, 'sac', 'Engrais', 'Sousse', 39, '2026-01-01', '2026-12-31', 0, 0.00, 100, 'sac'),
('Systeme Irrigation Goutte a Goutte', 'Kit complet irrigation goutte a goutte pour 1 hectare. Tuyaux, goutteurs, filtre et programmateur inclus.', 'VENTE', 'DISPONIBLE', 1200.00, 'unite', 'Irrigation', 'Sousse', 39, '2026-01-01', '2026-12-31', 0, 0.00, 5, 'unite');

-- Photos Ayoub
INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES
((SELECT id FROM annonces WHERE titre='Tracteur John Deere 6120M'), 'https://images.unsplash.com/photo-1530267981375-f0de937f5f13?w=400', 0),
((SELECT id FROM annonces WHERE titre='Engrais NPK 15-15-15 Premium'), 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400', 0),
((SELECT id FROM annonces WHERE titre='Systeme Irrigation Goutte a Goutte'), 'https://images.unsplash.com/photo-1563514227147-6d2ff665a6a0?w=400', 0);

-- 3. Annonces pour Amenallah Jerbi (id=40)
INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, categorie, localisation, proprietaire_id, date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution, quantite_disponible, unite_quantite)
VALUES
('Moissonneuse-Batteuse Claas Lexion', 'Moissonneuse-batteuse professionnelle, ideale pour ble et orge. Capacite tremie 9000L.', 'LOCATION', 'DISPONIBLE', 800.00, 'jour', 'Moissonneuse', 'Tunis', 40, '2026-03-01', '2026-09-30', 1, 1000.00, 2, 'piece'),
('Semences Ble Dur Karim', 'Semences certifiees de ble dur variete Karim, adaptees au climat tunisien. Rendement eleve.', 'VENTE', 'DISPONIBLE', 35.00, 'sac', 'Semences', 'Tunis', 40, '2026-01-01', '2026-06-30', 0, 0.00, 200, 'sac'),
('Pulverisateur Agricole 600L', 'Pulverisateur traine 600 litres avec rampe 12m. Parfait pour traitement phytosanitaire.', 'LOCATION', 'DISPONIBLE', 120.00, 'jour', 'Outil', 'Tunis', 40, '2026-01-01', '2026-12-31', 0, 200.00, 1, 'piece');

-- Photos Amenallah
INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES
((SELECT id FROM annonces WHERE titre='Moissonneuse-Batteuse Claas Lexion'), 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400', 0),
((SELECT id FROM annonces WHERE titre='Semences Ble Dur Karim'), 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=400', 0),
((SELECT id FROM annonces WHERE titre='Pulverisateur Agricole 600L'), 'https://images.unsplash.com/photo-1592982537447-6f2a6a0c7c10?w=400', 0);

-- 4. Annonces pour Badis Baji (id=44)
INSERT INTO annonces (titre, description, type, statut, prix, unite_prix, categorie, localisation, proprietaire_id, date_debut_disponibilite, date_fin_disponibilite, avec_operateur, caution, quantite_disponible, unite_quantite)
VALUES
('Olives Chemlali Bio - Recolte 2025', 'Olives fraiches variete Chemlali, agriculture biologique. Ideales pour huile ou conserve.', 'VENTE', 'DISPONIBLE', 8.00, 'kg', 'Fruits', 'Sfax', 44, '2026-01-01', '2026-03-31', 0, 0.00, 500, 'kg'),
('Charrue Reversible 3 Socs', 'Charrue reversible 3 socs pour labour profond. Compatible tracteurs 80-120 CV.', 'LOCATION', 'DISPONIBLE', 80.00, 'jour', 'Outil', 'Sfax', 44, '2026-01-01', '2026-12-31', 0, 100.00, 1, 'piece'),
('Dattes Deglet Nour Premium', 'Dattes Deglet Nour premiere qualite, recolte manuelle. Calibre AAA, caisses de 5kg.', 'VENTE', 'DISPONIBLE', 25.00, 'kg', 'Fruits', 'Tozeur', 44, '2026-01-01', '2026-06-30', 0, 0.00, 300, 'kg');

-- Photos Badis
INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES
((SELECT id FROM annonces WHERE titre='Olives Chemlali Bio - Recolte 2025'), 'https://images.unsplash.com/photo-1445282768818-728615cc910a?w=400', 0),
((SELECT id FROM annonces WHERE titre='Charrue Reversible 3 Socs'), 'https://images.unsplash.com/photo-1500595046743-cd271d694d30?w=400', 0),
((SELECT id FROM annonces WHERE titre='Dattes Deglet Nour Premium'), 'https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=400', 0);

SELECT CONCAT(COUNT(*), ' annonces inserees') as resultat FROM annonces;
SELECT CONCAT(COUNT(*), ' photos inserees') as photos FROM annonce_photos;
