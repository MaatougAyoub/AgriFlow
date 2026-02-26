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
    private String location;        // ✅ AJOUTÉ
    private double salary;          // ✅ AJOUTÉ
    private String publisher;       // ✅ AJOUTÉ
    private double salaryPerDay;
    private Double latitude;
    private Double longitude;
    // Constructeur vide
    public CollabRequest() {}

    // Constructeur avec tous les paramètres
    public CollabRequest(long requesterId, String title, String description,
                         LocalDate startDate, LocalDate endDate, int neededPeople,
                         String status, String location, double salary, String publisher) {
        this.requesterId = requesterId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.neededPeople = neededPeople;
        this.status = status;
        this.location = location;      // ✅ AJOUTÉ
        this.salary = salary;          // ✅ AJOUTÉ
        this.publisher = publisher;    // ✅ AJOUTÉ
    }

    // Constructeur simplifié (ancien, pour compatibilité)
    public CollabRequest(long requesterId, String title, String description,
                         LocalDate startDate, LocalDate endDate, int neededPeople, String status) {
        this(requesterId, title, description, startDate, endDate, neededPeople, status,
                "Non spécifié", 0.0, "Anonyme");
    }

    // ========== GETTERS ==========

    public long getId() {
        return id;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getNeededPeople() {
        return neededPeople;
    }

    public String getStatus() {
        return status;
    }

    // ✅ NOUVEAUX GETTERS
    public String getLocation() {
        return location != null ? location : "Non spécifié";
    }

    public double getSalary() {
        return salary;
    }

    public String getPublisher() {
        return publisher != null ? publisher : "Anonyme";
    }
    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    // ========== SETTERS ==========

    public void setId(long id) {
        this.id = id;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setNeededPeople(int neededPeople) {
        this.neededPeople = neededPeople;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ✅ NOUVEAUX SETTERS
    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return String.format("ID=%d | %s | %s | %s→%s | %d pers. | %.0f DT/jour | %s",
                id, title, location, startDate, endDate, neededPeople, salary, status);
    }
    public double getSalaryPerDay() {
        return salaryPerDay;
    }

    public void setSalaryPerDay(double salaryPerDay) {
        this.salaryPerDay = salaryPerDay;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

