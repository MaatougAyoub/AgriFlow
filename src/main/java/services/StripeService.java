package services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import entities.Reservation;

import java.awt.Desktop;
import java.net.URI;

// Service Paiement Stripe - nesta3mlou Stripe Checkout bech el user ykhallas online
// nkhalkou session paiement w nafthou el navigateur bech ykhallas
public class StripeService {

    // cle API Stripe (chargee depuis fichier config pour securite)
    private static String STRIPE_SECRET_KEY;

    public StripeService() {
        chargerCleAPI();
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    // nkharjou el cle men fichier stripe_config.txt (mech hardcoded fl code)
    private void chargerCleAPI() {
        try {
            java.io.File configFile = new java.io.File("stripe_config.txt");
            if (configFile.exists()) {
                STRIPE_SECRET_KEY = new String(java.nio.file.Files.readAllBytes(configFile.toPath())).trim();
                System.out.println("Clé Stripe chargée depuis stripe_config.txt");
            } else {
                System.err.println("⚠️ Fichier stripe_config.txt introuvable ! Créez-le avec votre clé Stripe.");
                STRIPE_SECRET_KEY = "";
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement clé Stripe : " + e.getMessage());
            STRIPE_SECRET_KEY = "";
        }
    }

    // nkhalkou session Stripe Checkout w nraj3ou el URL
    // el user yemchi ykhallas 3al page mta3 Stripe (securise)
    public String creerSessionPaiement(Reservation reservation) throws StripeException {
        // n7asbou el montant bel centimes (Stripe ykhdem bel plus petite unite : 1 EUR = 100 centimes)
        long montantCentimes = (long) (reservation.getPrixTotal() * 100);

        // nsemmou el produit bel titre mta3 l annonce
        String nomProduit = "Réservation AgriFlow";
        if (reservation.getAnnonce() != null && reservation.getAnnonce().getTitre() != null) {
            nomProduit = "Réservation - " + reservation.getAnnonce().getTitre();
        }

        // URLs de redirection apres paiement (Stripe exige https://)
        String successUrl = "https://example.com/paiement-reussi";
        String cancelUrl = "https://example.com/paiement-annule";

        // nebniw el session Checkout
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(montantCentimes)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(nomProduit)
                                                                .setDescription("Paiement via AgriFlow Marketplace")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    // nchoufou ken el paiement tmm (session paid)
    public boolean verifierPaiement(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return "paid".equals(session.getPaymentStatus());
    }

    // nafthou el navigateur mta3 el systeme bel URL mta3 Stripe Checkout
    public void ouvrirNavigateur(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback pour Windows
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", url});
                }
            }
        } catch (Exception e) {
            System.err.println("Impossible d'ouvrir le navigateur : " + e.getMessage());
        }
    }
}

