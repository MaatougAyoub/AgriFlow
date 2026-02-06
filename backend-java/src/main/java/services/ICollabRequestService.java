package services;

import entities.CollabRequest;
import java.sql.SQLException;
import java.util.List;

public interface ICollabRequestService extends IService<CollabRequest> {
    void updateStatus(long id, String status) throws SQLException;
    List<CollabRequest> findByStatus(String status) throws SQLException;
}
