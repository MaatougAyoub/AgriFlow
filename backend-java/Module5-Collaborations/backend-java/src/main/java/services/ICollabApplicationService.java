package services;

import entities.CollabApplication;
import java.sql.SQLException;
import java.util.List;

public interface ICollabApplicationService extends IService<CollabApplication> {
    List<CollabApplication> findByRequestId(long requestId) throws SQLException;
    void updateStatus(long id, String status) throws SQLException;
}
