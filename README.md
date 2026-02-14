# 🌱 AGRIFLOW — Module Marketplace

**Plateforme de Smart Farming pour la Tunisie**
*Projet PIDEV 3A — TeamSpark*

---

## 📋 Description

Module **Marketplace P2P** — location et vente de matériel agricole entre agriculteurs.

### Fonctionnalités CRUD
| Entité | Opérations |
|--------|-----------|
| **Annonces** | Créer, Lire, Modifier, Supprimer |
| **Réservations** | Réserver, Consulter, Annuler |

### Fonctionnalités Métier Avancé
- 🤖 **IA Gemini** — amélioration de description, suggestion de prix, modération
- 🛡️ **Anti-fraude** — détection automatique de contenu suspect
- 📄 **Contrats PDF** — génération automatique avec iText
- ✍️ **Signature automatique** sur les contrats

---

## 🚀 Installation

### 1. Base de données MySQL
```
1. Ouvrir phpMyAdmin (http://localhost/phpmyadmin)
2. Importer le fichier agriflow.sql (crée la BDD automatiquement)
```

### 2. Lancer dans IntelliJ
```
1. Ouvrir le projet dans IntelliJ IDEA
2. Build → Rebuild Project
3. Run Configuration → Main class : mains.AppLauncher
4. Cliquer sur ▶️ Run
```

> L'utilisateur simulé est **Amenallah Jerbi** (id=39, AGRICULTEUR)

---

## 📁 Structure du Projet

```
agriflow-marketplace/
├── src/main/java/
│   ├── controllers/    ← Contrôleurs JavaFX (7 fichiers)
│   ├── entities/       ← Entités : User, Annonce, Reservation, etc.
│   ├── services/       ← Services CRUD + IA + Anti-fraude
│   ├── utils/          ← MyDatabase (Singleton BDD)
│   └── mains/          ← AppLauncher + MainFX
├── src/main/resources/
│   ├── *.fxml          ← Vues JavaFX (7 fichiers)
│   ├── styles.css      ← Feuille de style
│   └── images/         ← Logo
├── src/test/java/      ← Tests JUnit
├── contrats/           ← Contrats PDF générés
├── agriflow.sql        ← Script BDD complet
└── pom.xml             ← Dépendances Maven
```

---

## 🔧 Technologies

| Technologie | Usage |
|------------|-------|
| Java 17 | Langage principal |
| JavaFX 21 | Interface graphique |
| MySQL | Base de données |
| JDBC | Connexion BDD |
| iText 7 | Génération PDF |
| Google Gemini API | IA Métier Avancé |
| JUnit 5 | Tests unitaires |
| Maven | Gestion de dépendances |

---

**TeamSpark — AGRIFLOW**
*Amenallah Jerbi — Marketplace*
