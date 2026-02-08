package entities;

import java.time.LocalDate;

public class CollabRequest {
    private long id;
    private long requesterId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int neededPeople;
    private String status;

    public CollabRequest() {}

    public CollabRequest(long requesterId, String title, String description, 
                         LocalDate startDate, LocalDate endDate, int neededPeople, String status) {
        this.requesterId = requesterId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.neededPeople = neededPeople;
        this.status = status;
    }

    // Getters/Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRequesterId() { return requesterId; }
    public void setRequesterId(long requesterId) { this.requesterId = requesterId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getNeededPeople() { return neededPeople; }
    public void setNeededPeople(int neededPeople) { this.neededPeople = neededPeople; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID=%d | %s | %s→%s | %d pers. | %s", 
            id, title, startDate, endDate, neededPeople, status);
    }
}
