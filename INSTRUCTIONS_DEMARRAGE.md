# 🌿 AGRIFLOW Marketplace — Guide de Démarrage

## Prérequis
- **IntelliJ IDEA** avec JDK 17+
- **MySQL** (XAMPP ou WampServer) sur `localhost:3306`, user `root`, pas de mot de passe
- **Maven** (intégré dans IntelliJ)

## 📦 Étape 1 — Créer la base de données

1. Lancer **XAMPP** → Démarrer **Apache** + **MySQL**
2. Ouvrir **phpMyAdmin** (`http://localhost/phpmyadmin`)
3. Créer une base de données nommée `agriflow` (si elle n'existe pas déjà)
4. Importer le fichier **`agriflow.sql`** (à la racine du projet)
   - Cliquer sur la base `agriflow` → Onglet **Importer** → Choisir `agriflow.sql` → **Exécuter**

> Si la base existe déjà, la supprimer d'abord (DROP) puis la recréer et importer.

## 💻 Étape 2 — Ouvrir le projet dans IntelliJ

1. **File → Open** → sélectionner le dossier `agriflow-marketplace`
2. IntelliJ détecte automatiquement le `pom.xml` Maven
3. Attendre que Maven télécharge les dépendances (barre de progression en bas)
4. Si demandé : **Trust Project** → Yes

## 🚀 Étape 3 — Lancer l'application

1. Naviguer vers `src/main/java/mains/AppLauncher.java`
2. **Clic droit** → **Run 'AppLauncher.main()'**
3. L'application se lance en tant que **Amenallah Jerbi** (AGRICULTEUR, id=39)

> Le point d'entrée est `AppLauncher.java`, pas `MainFX.java`.
> `AppLauncher` appelle `MainFX.main()` pour contourner l'erreur de module JavaFX.

## ✅ Étape 4 — Tester

### Tests unitaires
- Clic droit sur `src/test/java` → **Run All Tests**
- 4 classes de tests : `AnnonceTest`, `ReservationTest`, `AnnonceServiceTest`, `ReservationServiceTest`

### Test manuel
- Le Marketplace affiche les annonces de test avec images
- Navigation : Marketplace, Ajouter Annonce, Mes Réservations, Admin Dashboard
- Fonctionnalités AI : Amélioration de description, suggestion de prix, modération
- Génération de contrats PDF dans le dossier `contrats/`

## 📁 Architecture du projet

```
src/main/java/
├── mains/          → AppLauncher.java, MainFX.java (point d'entrée)
├── entities/       → User, Annonce, Reservation, Message, PhotoAnnonce, enums
├── services/       → AnnonceService, ServiceReservation, UserService, MessageService, etc.
├── controllers/    → MainController, MarketplaceController, etc.
└── utils/          → MyDatabase.java (Singleton JDBC)

src/main/resources/
├── *.fxml          → Vues JavaFX (Main, Marketplace, AjouterAnnonce, etc.)
├── images/         → Logo et assets
└── sql/schema.sql  → Script de création des tables Marketplace
```

## 🗄️ Base de données — Tables Marketplace

| Table | Description |
|-------|-------------|
| `utilisateurs` | Utilisateurs (partagée avec module Ayoub) |
| `annonces` | Annonces de location/vente de matériel agricole |
| `annonce_photos` | Photos associées aux annonces |
| `reservations` | Réservations P2P entre agriculteurs |
| `messages` | Messagerie P2P entre utilisateurs |

## 👤 Membre responsable
**Amenallah Jerbi** — Lead Tech & Marketplace — TeamSpark
