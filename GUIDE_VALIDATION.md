# 🎓 Guide de Validation - Module Marketplace AgriFlow
## Pour Amenallah Jerbi - Présentation au Prof

---

## 📁 STRUCTURE DU MODULE (où se trouve quoi)

| Dossier | Contenu |
|---------|---------|
| `entities/` | Les classes Java qui représentent les tables de la BDD |
| `services/` | La logique métier (CRUD, IA, paiement, validation) |
| `controllers/` | Le lien entre l'interface (FXML) et les services |
| `resources/` | Les fichiers FXML (l'interface graphique) |

---

## 🧩 FICHIERS DU MODULE MARKETPLACE

### ENTITIES (les objets Java = les tables MySQL)

| Fichier | C'est quoi | Colonnes principales |
|---------|-----------|---------------------|
| `Annonce.java` | Une annonce sur le marketplace | titre, description, type, prix, proprietaire_id, quantite_disponible |
| `Reservation.java` | Une demande de réservation | annonce, demandeur, dateDebut, dateFin, quantite, prixTotal, paiementEffectue |
| `TypeAnnonce.java` | Enum : LOCATION ou VENTE | - |
| `StatutAnnonce.java` | Enum : DISPONIBLE, RESERVE, etc. | - |
| `StatutReservation.java` | Enum : EN_ATTENTE, ACCEPTEE, REFUSEE | - |

### SERVICES (la logique)

| Fichier | Rôle | Méthodes clés |
|---------|------|--------------|
| `AnnonceService.java` | CRUD annonces | `ajouter()`, `modifier()`, `supprimer()`, `recupererDisponibles()`, `decrementerQuantite()` |
| `ServiceReservation.java` | CRUD réservations | `ajouter()`, `accepterReservation()`, `refuserReservation()`, `marquerPaiement()` |
| `GeminiAIService.java` | Appels Google Gemini | `ameliorerDescription()`, `suggererPrix()`, `modererContenu()` |
| `ContentValidator.java` | Validation locale | `validerAnnonce()` - vérifie champs obligatoires, mots interdits, prix |
| `FraudControlService.java` | Anti-fraude | `checkAnnonce()` - vérifie titre, prix, mots suspects |
| `StripeService.java` | Paiement en ligne | `creerSessionPaiement()`, `verifierPaiement()`, `ouvrirNavigateur()` |

### CONTROLLERS (interface ↔ logique)

| Fichier | Page | Ce qu'il fait |
|---------|------|--------------|
| `MarketplaceController.java` | Marketplace.fxml | Affiche les annonces en grille, recherche, filtre par type, lance la musique |
| `AnnonceCardController.java` | AnnonceCard.fxml | Gère UNE carte d'annonce (image, prix, boutons) |
| `AjouterAnnonceController.java` | AjouterAnnonce.fxml | Formulaire pour créer/modifier une annonce + boutons IA |
| `ReservationDialogController.java` | ReservationDialog.fxml | Popup pour réserver (dates, quantité, prix auto) |
| `MesReservationsController.java` | MesReservations.fxml | Liste des réservations + boutons Accepter/Refuser/Payer/PDF |

---

## 🔑 QUESTIONS PROBABLES DE LA PROF + RÉPONSES

### "Comment fonctionne le CRUD des annonces ?"
> **Réponse :** Dans `AnnonceService.java`. La méthode `ajouter()` fait un INSERT SQL, `modifier()` un UPDATE, `supprimer()` un DELETE, `recupererDisponibles()` un SELECT avec filtre `quantite_disponible > 0`. Chaque méthode utilise un `PreparedStatement` pour éviter les injections SQL.

### "Comment marche l'IA ?"
> **Réponse :** On utilise l'API Google Gemini dans `GeminiAIService.java`. On envoie un prompt (texte) via HTTP POST à Google, et on récupère la réponse en JSON. 3 fonctions : améliorer la description, suggérer un prix, modérer le contenu. L'appel est asynchrone (dans un Thread séparé) pour ne pas bloquer l'interface.

### "Pourquoi un Thread pour l'IA ?"
> **Réponse :** Parce que l'appel API prend du temps (2-5 secondes). Si on le fait sur le thread principal (JavaFX Application Thread), l'interface se fige. Avec `new Thread(() -> {...}).start()` et `Platform.runLater()`, on fait l'appel en arrière-plan et on met à jour l'interface quand c'est fini.

### "Comment fonctionne le paiement Stripe ?"
> **Réponse :** Dans `StripeService.java`. On crée une session Stripe Checkout avec le montant en centimes (EUR). Stripe nous donne une URL, on ouvre le navigateur dessus. L'utilisateur paye sur la page sécurisée de Stripe. Ensuite il revient dans l'app et confirme. On enregistre le paiement dans la BDD.

### "La clé Stripe est où ?"
> **Réponse :** Dans un fichier externe `stripe_config.txt` (pas dans le code pour la sécurité). La méthode `chargerCleAPI()` lit ce fichier au démarrage. Le fichier est dans `.gitignore` donc il n'est pas pushé sur GitHub.

### "Comment marche la validation des annonces ?"
> **Réponse :** Il y a 3 niveaux : (1) `ContentValidator` vérifie les champs obligatoires et mots interdits localement, (2) `FraudControlService` vérifie le prix et les mots suspects, (3) `GeminiAIService.modererContenu()` utilise l'IA pour détecter les contenus inappropriés.

### "Comment fonctionne la gestion de stock ?"
> **Réponse :** Chaque annonce a un champ `quantite_disponible`. Quand quelqu'un réserve, la méthode `decrementerQuantite()` dans `AnnonceService` fait un UPDATE pour réduire la quantité. Si la quantité tombe à 0, l'annonce n'apparaît plus dans le marketplace (filtrée par `recupererDisponibles()`).

### "Comment sont affichées les annonces ?"
> **Réponse :** Le `MarketplaceController` charge les annonces avec `recupererDisponibles()`, puis pour chaque annonce, il charge un `AnnonceCard.fxml` via un `FXMLLoader`. Les cartes sont affichées dans un `FlowPane` (grille responsive). La recherche filtre en temps réel avec un listener sur le `TextField`.

### "Comment marche la réservation ?"
> **Réponse :** L'utilisateur clique "Réserver" sur une carte. Un dialog s'ouvre (`ReservationDialog.fxml`) où il choisit les dates et la quantité. Le prix total est calculé automatiquement. Le propriétaire peut accepter ou refuser. Si accepté, la quantité est décrémentée et le bouton "Payer" apparaît.

### "Architecture du projet ?"
> **Réponse :** Architecture MVC en 3 couches : **Entities** (modèle de données), **Services** (logique métier + accès BDD), **Controllers** (lien interface-logique). Connexion BDD via singleton `MyDatabase` (pattern Singleton pour 1 seule connexion). Les vues sont en FXML (séparation interface/logique).

---

## ⚡ MOTS CLÉS TECHNIQUES À RETENIR

| Mot | Signification simple |
|-----|---------------------|
| **PreparedStatement** | Requête SQL sécurisée (anti injection SQL) |
| **Singleton** | Pattern qui garantit 1 seule instance (MyDatabase) |
| **FXML** | Fichier XML qui décrit l'interface graphique JavaFX |
| **FXMLLoader** | Charge un fichier FXML et crée les objets graphiques |
| **FlowPane** | Conteneur JavaFX qui arrange les éléments en grille |
| **Platform.runLater()** | Exécute du code sur le thread JavaFX (après un Thread) |
| **MediaPlayer** | Classe JavaFX pour jouer de la musique/vidéo |
| **Stripe Checkout** | Page de paiement sécurisée hébergée par Stripe |
| **API REST** | Communication avec un serveur web via HTTP (GET, POST) |
| **JSON** | Format de données texte (utilisé par Gemini et Stripe) |

---

## 🎯 FLUX PRINCIPAL À DÉMONTRER

1. **Connexion** → Page SignIn (email + mot de passe)
2. **Marketplace** → Voir les annonces + recherche + filtre (🎵 musique)
3. **Ajouter annonce** → Formulaire + bouton IA "Améliorer description"  + "Suggérer prix"
4. **Réserver** → Choisir dates + quantité → prix auto-calculé
5. **Accepter/Refuser** → Le propriétaire gère dans "Mes Réservations"
6. **Payer** → Bouton "💳 Payer" → Stripe Checkout → Confirmation
7. **PDF** → Générer contrat PDF après paiement

Bonne chance pour la validation ! 💪🎓
