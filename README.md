# 🌱 AgriFlow
An integrated university project: an AgriTech web platform for smart agricultural management.

The platform aims to digitize and optimize agricultural management through smart and accessible digital solutions.
**Plateforme de Smart Farming pour la Tunisie**
*Projet PIDEV 3A — TeamSpark*
---


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

## 👥 Target Users
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
4. Cliquer sur Run
```

> L'utilisateur simulé est **Amenallah Jerbi** (id=39, AGRICULTEUR)

---

## ✉️ Envoi des codes par email (MailerSend)

L'application envoie un **code** par email dans ces écrans :
- Inscription (SignUp)
- Mot de passe oublié
- Modification du profil

### 1) Pré-requis côté MailerSend
1. Créer un compte MailerSend
2. Aller dans **Email → Sender identities**
3. Vérifier un **email** (le plus simple) ou un **domaine** (recommandé en prod)
4. Générer un token dans **Settings → API tokens** (permissions Email)

### 2) Configuration côté projet (variables d'environnement)
Le projet lit les variables suivantes au moment de l'envoi :
- `MAILERSEND_API_KEY` : token API MailerSend
- `MAILERSEND_FROM_EMAIL` : email expéditeur (doit être vérifié sur MailerSend)
- `MAILERSEND_FROM_NAME` : (optionnel) nom expéditeur, par défaut `AgriFlow`

#### Windows (PowerShell) — temporaire (pour la session courante)
```powershell
$env:MAILERSEND_API_KEY = "VOTRE_TOKEN"
$env:MAILERSEND_FROM_EMAIL = "no-reply@votre-domaine.tld"
$env:MAILERSEND_FROM_NAME = "AgriFlow"
```

#### Windows — permanent
```powershell
setx MAILERSEND_API_KEY "VOTRE_TOKEN"
setx MAILERSEND_FROM_EMAIL "no-reply@votre-domaine.tld"
setx MAILERSEND_FROM_NAME "AgriFlow"
```

> Après `setx`, relancer l'IDE/terminal pour que les variables soient prises en compte.

- **Farmers**: manage their profiles, parcels, and agricultural activities
- **Administrator**: manage users, validate data, and monitor the platform
- **Experts**: 
---

## ⚙️ Main Features

- User authentication and role management (Admin / Farmer)
- Farmer profile management
- Agricultural data management (parcels, crops, etc.)
- Secure data storage
- Admin dashboard for monitoring and validation
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

## 🛠️ Technologies Used
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

## 🗂️ Project Structure





**TeamSpark — AGRIFLOW**

