package services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceAgriculteur <T>{
    void ajouterAgriculteur (T t) throws SQLException;
    void modifierAgriculteur (T t) throws SQLException;
    void supprimerAgriculteur (T t) throws SQLException;
    List<T> recupererAgriculteurs() throws SQLException;
}
