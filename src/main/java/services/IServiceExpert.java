package services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceExpert <T>{
    void ajouterExpert (T t) throws SQLException;
    void modifierExpert (T t) throws SQLException;
    void supprimerExpert (T t) throws SQLException;
    List<T> recupererExpert() throws SQLException;



}
