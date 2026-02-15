package services;

import java.sql.SQLException;
import java.util.List;

// Interface CRUD generique - kol service lezem yimplementi hedhi (ajouter, modifier, supprimer, recuperer)
// ya3ni kol service yebda 3andou nes les methodes (polymorphisme)
public interface IService<T> {

    void ajouter(T entity) throws SQLException;

    void modifier(T entity) throws SQLException;

    void supprimer(T entity) throws SQLException;

    List<T> recuperer() throws SQLException;

    T recupererParId(int id) throws SQLException;
}