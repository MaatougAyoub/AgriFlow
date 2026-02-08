# AGRIFLOW - Backend Java (Module 5: Collaborations)

## Prérequis
- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.x** via **XAMPP**
- **IntelliJ IDEA** (recommandé)

## Configuration

### 1. Base de données
1. Démarre **XAMPP** → Lance MySQL
2. Ouvre **MySQL Workbench** ou **phpMyAdmin**
3. Exécute le script : `db/module5_collaborations.sql`
4. Vérifie que la base `agriflow` et les tables existent

### 2. Configuration JDBC
Le fichier `DBConnection.java` est configuré pour XAMPP par défaut :
- Host: `localhost:3306`
- User: `root`
- Password: *(vide)*

Si ton mot de passe MySQL est différent, modifie `DBConnection.java`.

## Lancer le projet

### Avec IntelliJ IDEA
1. Ouvre le dossier `backend-java` comme projet Maven
2. Laisse Maven télécharger les dépendances
3. Lance `Main.java` (clic droit → Run)

### Avec ligne de commande
```bash
cd backend-java
mvn clean compile exec:java -Dexec.mainClass="tn.esprit.agriflow.Main"
```

## Fonctionnalités (Séance 3 - JDBC CRUD)
- ✅ Créer/Lister/Modifier/Supprimer demandes de collaboration
- ✅ Créer/Lister/Modifier/Supprimer candidatures
- ✅ Menu console interactif
- ✅ Connexion JDBC MySQL (XAMPP)

## Structure du code
```
src/main/java/tn/esprit/agriflow/
├── config/
│   └── DBConnection.java          (Connexion MySQL singleton)
├── collab/
│   ├── CollabRequest.java         (Model demande)
│   ├── CollabApplication.java     (Model candidature)
│   ├── CollabRequestDAO.java      (CRUD demandes)
│   ├── CollabApplicationDAO.java  (CRUD candidatures)
│   └── CollabConsoleApp.java      (Application console)
└── Main.java                      (Point d'entrée)
```

## Auteur
**Membre 5** - Module Collaborations (recherche partenaires)  
PIDEV 2026 - ESPRIT
