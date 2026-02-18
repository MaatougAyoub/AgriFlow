package services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceReclamation<T> {
    void ajouterReclamation (T t) throws SQLException;
    void modifierReclamation (T t) throws SQLException;
    void supprimerReclamation (T t) throws SQLException;
    List<T> recupererReclamation() throws SQLException;


}
