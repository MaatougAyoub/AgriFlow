package services;

import java.sql.SQLException;
import java.util.List;


public interface IServiceB<T> {


    //badis
    void ajouter(T t) throws SQLException;

    //badis
    void modifier(T t) throws SQLException;

    //badis
    void supprimer(T t) throws SQLException;

    //badis //amen
    List<T> recuperer() throws SQLException;


}