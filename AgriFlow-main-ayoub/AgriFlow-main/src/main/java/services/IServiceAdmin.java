package services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceAdmin <T> {
    void ajouterAdmin (T t) throws SQLException;
    void modifierAdmin (T t) throws SQLException;
    void supprimerAdmin (T t) throws SQLException;
    List<T> recupererAdmin() throws SQLException;

}
