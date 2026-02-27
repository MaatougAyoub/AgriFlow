# Module 5 - AgriFlow Collaborations - Nouvelles Interfaces

Ce document décrit les 6 nouvelles interfaces modernes avec design "cards" ajoutées au projet AgriFlow Module 5.

## 🚀 Lancement de l'application

### Option 1 : Lancer les nouvelles interfaces (MainFX)
```bash
cd backend-java
mvn javafx:run -Djavafx.mainClass=mains.MainFX
```

### Option 2 : Lancer l'ancienne interface (MainApp) - Compatibilité
```bash
cd backend-java
mvn javafx:run
```

## 📁 Structure du projet

```
backend-java/
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── entities/           ✅ Existant - préservé
    │   │   ├── services/           ✅ Existant - préservé
    │   │   ├── utils/              ✅ Existant - préservé
    │   │   ├── validators/         ✅ Existant - préservé
    │   │   ├── controllers/        ✅ Enrichi avec 6 nouveaux contrôleurs
    │   │   └── mains/              🆕 Nouveau - MainFX.java
    │   └── resources/
    │       ├── fxml/               🆕 6 nouvelles interfaces FXML
    │       └── css/                🆕 styles.css moderne
    └── test/
        └── java/                   ✅ Existant - préservé
```

## 🎨 Les 6 nouvelles interfaces

### 1. **ExploreCollaborations.fxml** - Explorer les collaborations
- **Description** : Page d'accueil avec recherche et filtres
- **Controller** : `ExploreCollaborationsController.java`
- **Fonctionnalités** :
  - Barre de recherche
  - Filtres (lieu, dates, type, statut, salaire)
  - Grid 2x3 de cards avec détails des demandes
  - Badges de statut colorés (VALIDÉ, EN ATTENTE, etc.)
  - Navigation vers toutes les autres vues

### 2. **CollabRequestDetails.fxml** - Détails d'une demande
- **Description** : Vue détaillée d'une demande de collaboration
- **Controller** : `CollabRequestDetailsController.java`
- **Fonctionnalités** :
  - Affichage complet des informations (lieu, dates, salaire, etc.)
  - Description détaillée
  - Bouton "Postuler"
  - Informations sur le demandeur

### 3. **PublishRequest.fxml** - Publier une demande
- **Description** : Formulaire de création d'une nouvelle demande
- **Controller** : `PublishRequestController.java`
- **Fonctionnalités** :
  - Formulaire complet avec validation
  - Champs : titre, lieu, dates, nombre de personnes, salaire, description
  - Statut automatique : EN ATTENTE
  - Validation des données avant soumission

### 4. **MyRequests.fxml** - Mes demandes
- **Description** : Liste des demandes publiées par l'utilisateur
- **Controller** : `MyRequestsController.java`
- **Fonctionnalités** :
  - Affichage des demandes avec leur statut
  - Liste des candidatures reçues pour chaque demande
  - Actions : voir détails, supprimer
  - Badges de statut (VALIDÉE, EN ATTENTE, CLÔTURÉE, REJETÉE)

### 5. **MyApplications.fxml** - Mes candidatures
- **Description** : Liste des candidatures de l'utilisateur
- **Controller** : `MyApplicationsController.java`
- **Fonctionnalités** :
  - Affichage des candidatures avec leur statut
  - Actions : voir la demande, retirer candidature
  - Section informative sur les statuts
  - Badges colorés

### 6. **AdminValidation.fxml** - Validation admin
- **Description** : Interface administrateur pour valider les demandes
- **Controller** : `AdminValidationController.java`
- **Fonctionnalités** :
  - Onglets de navigation (Utilisateurs, Parcelles, Irrigation, etc.)
  - Filtre des demandes en attente
  - Tableau avec actions : Valider, Rejeter, Voir détail
  - Gestion de toutes les demandes

## 🎨 Design moderne

### Styles CSS (styles.css)
- **Cards** : Cartes blanches avec ombres portées (dropshadow)
- **Badges** : Badges colorés arrondis pour les statuts
  - Vert : VALIDÉ / ACCEPTÉ
  - Jaune : EN ATTENTE
  - Rouge : REJETÉ
  - Gris : CLÔTURÉ
- **Boutons** : 3 types avec hover effects
  - `btn-primary` : Vert (#2E7D32)
  - `btn-orange` : Orange (#FF9800)
  - `btn-secondary` : Gris (#9E9E9E)
- **Header** : Dégradé vert avec titre blanc
- **Grid layout** : Espacement uniforme (20px)

## 🔄 Navigation

Le système de navigation est géré par `MainFX.java` avec 6 méthodes statiques :
- `showExploreCollaborations()` - Page d'accueil
- `showCollabRequestDetails()` - Détails d'une demande
- `showPublishRequest()` - Formulaire de publication
- `showMyRequests()` - Mes demandes
- `showMyApplications()` - Mes candidatures
- `showAdminValidation()` - Interface admin

Chaque contrôleur peut appeler ces méthodes pour naviguer entre les vues.

## ✅ Compatibilité

- ✅ Tous les fichiers existants sont préservés
- ✅ L'ancienne interface (MainApp.java) fonctionne toujours
- ✅ Tous les tests passent (42 tests, 0 erreurs)
- ✅ Les services, entités et validateurs sont inchangés
- ✅ Compilation réussie avec Maven

## 🧪 Tests

```bash
cd backend-java
mvn test
```

Résultat attendu :
```
Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 📝 Notes techniques

- **Java 17** : Version minimum requise
- **JavaFX 21** : Framework UI
- **CSS externe** : Styles séparés pour maintenance facile
- **Navigation statique** : Accès facile depuis tous les contrôleurs
- **Validation** : Utilise les validateurs existants (CollabRequestValidator)

## 🎯 Prochaines étapes

Pour une utilisation en production :
1. Intégrer l'authentification utilisateur
2. Passer l'ID utilisateur aux contrôleurs
3. Implémenter la pagination pour les listes
4. Ajouter des filtres fonctionnels
5. Connecter les données réelles aux cards
6. Ajouter des tests d'intégration pour les contrôleurs

---

**Développé par : YakineEddine**  
**Date : Février 2026**  
**Projet : AgriFlow - Module 5 Collaborations**
