# 🚀 AGRIFLOW MARKETPLACE - Instructions de Démarrage

## ✅ Projet Prêt pour Validation Sprint 0

---

## 📋 PRÉREQUIS

Avant de démarrer, assurez-vous d'avoir :

- ✅ **Java 17** ou supérieur installé
- ✅ **Maven 3.8+** installé
- ✅ **MySQL 8.0+** installé et démarré
- ✅ **IntelliJ IDEA** (recommandé) ou tout autre IDE Java

---

## 🗄️ ÉTAPE 1 : Création de la Base de Données

### Option A : Via MySQL Workbench (Recommandé)

1. Ouvrez **MySQL Workbench**
2. Connectez-vous à votre serveur MySQL (localhost, user: `root`, password: vide ou votre mot de passe)
3. Allez dans **File → Open SQL Script**
4. Sélectionnez le fichier : `src/main/resources/sql/schema.sql`
5. Cliquez sur l'éclair ⚡ pour exécuter tout le script
6. Vérifiez que vous voyez les messages de succès :
   ```
   ✅ Base de données AgriFlow créée avec succès!
   👤 3 utilisateurs insérés avec succès
   📢 5 annonces disponibles pour validation
   ```

### Option B : Via Ligne de Commande

```bash
# Depuis le répertoire racine du projet
mysql -u root < src/main/resources/sql/schema.sql

# OU avec mot de passe :
mysql -u root -p < src/main/resources/sql/schema.sql
```

### ✅ Vérification de la Base de Données

Connectez-vous à MySQL et vérifiez :

```sql
USE agriflow;
SHOW TABLES;  -- Doit afficher 5 tables

SELECT COUNT(*) FROM users;     -- Doit retourner 3
SELECT COUNT(*) FROM annonces;  -- Doit retourner 5
```

---

## 🔧 ÉTAPE 2 : Configuration du Projet

### A. Vérifier la Connexion MySQL

Ouvrez le fichier : `src/main/java/com/agriflow/marketplace/utils/MyDatabase.java`

Vérifiez la configuration (lignes 12-14) :

```java
private static final String URL =
        "jdbc:mysql://localhost:3306/agriflow?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";  // Changez si vous avez un mot de passe
```

**⚠️ IMPORTANT** : Si vous avez un mot de passe MySQL, modifiez la ligne `PASSWORD = ""` en `PASSWORD = "votre_mot_de_passe"`

### B. Recharger les Dépendances Maven

Dans IntelliJ IDEA :
1. Clic droit sur `pom.xml`
2. **Maven → Reload Project**

OU en ligne de commande :
```bash
mvn clean install
```

---

## ▶️ ÉTAPE 3 : Lancer l'Application

### Option A : Depuis IntelliJ IDEA (Recommandé)

1. Ouvrez le fichier : `src/main/java/com/agriflow/marketplace/Main.java`
2. Clic droit sur le fichier
3. Sélectionnez **Run 'Main.main()'**
4. L'application JavaFX devrait démarrer et afficher le Marketplace avec les 5 annonces

### Option B : Via Maven

```bash
mvn clean javafx:run
```

---

## 🎯 ÉTAPE 4 : Tester les Fonctionnalités

### ✅ Test 1 : Affichage des Annonces

- L'application devrait afficher **5 annonces** de tracteurs et moissonneuses
- Vérifiez que chaque annonce affiche : titre, prix, localisation, marque

### ✅ Test 2 : Créer une Réservation

1. Cliquez sur une annonce (ex: "Tracteur John Deere 6130R")
2. Cliquez sur le bouton **"Réserver"**
3. Remplissez le formulaire :
   - Date début : `2025-03-15`
   - Date fin : `2025-03-20`
   - Message : "Je souhaite louer ce tracteur pour les semis"
4. Cliquez sur **"Confirmer la Réservation"**
5. **VÉRIFICATION AUTOMATIQUE** :
   - ✅ Calcul automatique : `prix_total = 5 jours × 250 DT = 1250 DT`
   - ✅ Commission 10% : `125 DT`
   - ✅ Statut : `EN_ATTENTE`

### ✅ Test 3 : Vérifier en Base de Données

Ouvrez MySQL Workbench et exécutez :

```sql
USE agriflow;

-- Vérifier la réservation créée
SELECT 
    r.id,
    a.titre AS annonce,
    r.date_debut,
    r.date_fin,
    r.prix_total,
    r.commission,
    r.statut
FROM reservations r
INNER JOIN annonces a ON r.annonce_id = a.id
ORDER BY r.id DESC
LIMIT 1;
```

Vous devriez voir :
- `prix_total = 1250.00`
- `commission = 125.00`
- `statut = EN_ATTENTE`

---

## 📊 VALIDATION ACADÉMIQUE - Checklist Sprint 0

### ✅ Architecture MVC
- ☑️ **Models** : Annonce, Reservation, User, etc. → `src/main/java/com/agriflow/marketplace/models/`
- ☑️ **Views** : FXML → `src/main/resources/com/agriflow/marketplace/views/`
- ☑️ **Controllers** : MarketplaceController, etc. → `src/main/java/com/agriflow/marketplace/controllers/`
- ☑️ **Services** : AnnonceService, ReservationService → `src/main/java/com/agriflow/marketplace/services/`

### ✅ JDBC Natif (Pas de Hibernate/JPA)
- ☑️ **Singleton** : `MyDatabase.java` avec `getInstance()`
- ☑️ **PreparedStatement** : Toutes les requêtes (INSERT, UPDATE, DELETE, SELECT)
- ☑️ **Imports** : `java.sql.*` partout
- ☑️ **Gestion Exceptions** : `try/catch SQLException` propre

### ✅ Logique Métier Lead Tech
- ☑️ **Calcul automatique** : `prix_total = durée × prix_jour` → `ReservationService.ajouterReservation()` ligne 40-44
- ☑️ **Commission 10%** : `commission = prix_total × 0.10` → `ReservationService.ajouterReservation()` ligne 47-49
- ☑️ **Statut par défaut** : `EN_ATTENTE` → `ReservationService.ajouterReservation()` ligne 52
- ☑️ **Génération PDF** : `ContratPDFService.java` avec iText 7 + affichage commission

### ✅ Sécurité
- ☑️ **PreparedStatement** : Protection injection SQL
- ☑️ **Contraintes FK** : En base de données
- ☑️ **Validation** : Contrôles dans les services

---

## 🏆 RÉCAPITULATIF FINAL

| Composant | Statut | Emplacement |
|-----------|--------|-------------|
| **Base de données** | ✅ | `agriflow` avec 5 tables |
| **Connexion JDBC** | ✅ | `MyDatabase.java` (Singleton) |
| **CRUD Annonces** | ✅ | `AnnonceService.java` |
| **Logique Réservation** | ✅ | `ReservationService.java` (ajouterReservation) |
| **PDF Contrats** | ✅ | `ContratPDFService.java` (iText 7) |
| **Interface JavaFX** | ✅ | `Main.java` + Controllers + FXML |
| **Données de test** | ✅ | 3 users + 5 annonces |
| **Compilation** | ✅ | Build réussi sans erreur |

---

## 🐛 Résolution des Problèmes Courants

### Problème 1 : "Access denied for user 'root'@'localhost'"

**Solution** : Modifiez le mot de passe dans `MyDatabase.java` ligne 15 :
```java
private static final String PASSWORD = "votre_mot_de_passe";
```

### Problème 2 : "Unknown database 'agriflow'"

**Solution** : Exécutez le script SQL : `mysql -u root < src/main/resources/sql/schema.sql`

### Problème 3 : "Communications link failure"

**Solution** : Vérifiez que MySQL est démarré :
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
```

### Problème 4 : "module not found: com.mysql.cj"

**Solution** : Ce problème a été corrigé. La ligne `requires com.mysql.cj;` a été retirée du `module-info.java`

### Problème 5 : Build échoue

**Solution** :
```bash
# Nettoyer et recompiler
mvn clean install -U

# Puis dans IntelliJ : File → Invalidate Caches / Restart
```

---

## 📞 Support

Si vous rencontrez des problèmes :

1. Vérifiez que MySQL est démarré
2. Vérifiez que la base `agriflow` existe : `SHOW DATABASES;`
3. Vérifiez les logs dans la console IntelliJ
4. Rebuild le projet : `Build → Rebuild Project`

---

## 🎉 Félicitations !

Votre application **AGRIFLOW Marketplace** est maintenant **100% fonctionnelle** et prête pour la **validation du Sprint 0** !

Vous pouvez maintenant :
- ✅ Afficher les annonces de matériel agricole
- ✅ Créer des réservations avec calcul automatique
- ✅ Voir les contrats PDF générés
- ✅ Démontrer l'architecture JDBC académique

**Bon courage pour votre présentation ! 🚀**
