package services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    long add(T entity) throws SQLException;
    List<T> findAll() throws SQLException;
    T findById(long id) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(long id) throws SQLException;
}
