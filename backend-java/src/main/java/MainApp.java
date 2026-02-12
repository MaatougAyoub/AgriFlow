import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application JavaFX
 * Module 5 - Gestion des Collaborations
 * 
 * @author YakineEddine
 * @version 1.0
 */
public class MainApp extends Application {
    
    /**
     * Méthode principale de démarrage de l'application JavaFX
     * Charge l'interface FXML et configure la fenêtre
     * 
     * @param primaryStage La fenêtre principale de l'application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Créer la scène avec les dimensions
            Scene scene = new Scene(root, 1100, 750);
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("AgriFlow - Module Collaborations");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Afficher la fenêtre
            primaryStage.show();
            
            System.out.println("✅ Application JavaFX lancée avec succès !");
            System.out.println("📊 Module 5 - Gestion des Collaborations");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur au lancement de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode appelée lors de la fermeture de l'application
     * Permet de nettoyer les ressources si nécessaire
     */
    @Override
    public void stop() {
        System.out.println("🛑 Fermeture de l'application AgriFlow...");
    }
    
    /**
     * Point d'entrée principal du programme
     * Lance l'application JavaFX
     * 
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de l'application AgriFlow...");
        launch(args);
    }
}
