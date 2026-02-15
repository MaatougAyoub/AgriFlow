package services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    //amen
    void ajouter(T entity) throws SQLException;
    //badis
    //void ajouter(T t) throws SQLException;
    //amen
    void modifier(T entity) throws SQLException;
    //badis
    //void modifier(T t) throws SQLException;
    //amen
    void supprimer(T entity) throws SQLException;
    //badis
    //void supprimer(T t) throws SQLException;

    //badis et amen
    List<T> recuperer() throws SQLException;



    //amen
    T recupererParId(int id) throws SQLException;
}