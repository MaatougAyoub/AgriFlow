# 🌱 AGRIFLOW - Module Marketplace

**Plateforme de Smart Farming pour la Tunisie**  
*Projet PIDEV - TeamSpark*

---

## 📋 Description

Module Marketplace de location et vente de matériel agricole en **Peer-to-Peer (P2P)**.

### Fonctionnalités

- 🚜 **Louer** du matériel agricole (tracteurs, moissonneuses, etc.)
- 🌾 **Vendre** des produits agricoles (engrais, semences, récoltes)
- 📋 **Gérer les réservations** entre agriculteurs
- ✍️ **Signature automatique** sur les contrats PDF

---

## 🗄️ Structure de la Base de Données

| Table | Description |
|-------|-------------|
| `users` | Agriculteurs (avec signature_image pour signature auto) |
| `annonces` | Annonces de location/vente |
| `annonce_photos` | Photos des annonces |
| `reservations` | Réservations entre agriculteurs |
| `messages` | Messagerie P2P |

---

## 🚀 Installation

### 1. Configurer la base de données

```bash
mysql -u root -p < sql/marketplace_schema.sql
```

### 2. Configurer JavaFX dans IntelliJ

- Ajouter le SDK JavaFX 17 aux librairies
- VM Options: `--module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml`

### 3. Lancer l'application

```bash
mvn clean compile
mvn javafx:run
```

---

## ✍️ Signature Automatique

```
1. Ayoub enregistre la signature dans users.signature_image
2. Amenallah génère le contrat PDF avec iText
3. La signature est intégrée automatiquement !
```

---

## 📁 Structure du Projet

```
agriflow-marketplace/
├── src/main/java/com/agriflow/marketplace/
│   ├── Main.java
│   ├── models/ (User, Annonce, Reservation, enums)
│   ├── services/ (CRUD + ContratPDFService)
│   ├── controllers/ (JavaFX)
│   └── utils/ (MyDatabase)
├── src/main/resources/
│   └── views/ (FXML)
├── sql/marketplace_schema.sql
└── pom.xml
```

---

**TeamSpark - AGRIFLOW**  
*Smart Farming Tunisia*
