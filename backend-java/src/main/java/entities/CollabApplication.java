package entities;

public class CollabApplication {
    private long id;
    private long requestId;
    private long candidateId;
    private String message;
    private String status;

    public CollabApplication() {}

    public CollabApplication(long requestId, long candidateId, String message, String status) {
        this.requestId = requestId;
        this.candidateId = candidateId;
        this.message = message;
        this.status = status;
    }

    // Getters/Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }
    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID=%d | Request=%d | Candidat=%d | '%s' | %s", 
            id, requestId, candidateId, message, status);
    }
}
