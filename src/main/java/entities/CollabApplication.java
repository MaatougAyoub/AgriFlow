package entities;

public class CollabApplication {
    private long id;
    private long candidateId;  // ✅ Changé de userId
    private long requestId;
    private String fullName;
    private String phone;
    private String email;
    private int yearsOfExperience;
    private String motivation;
    private double expectedSalary;
    private String status;

    // Constructeur vide
    public CollabApplication() {}

    // Getters/Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCandidateId() { return candidateId; }  // ✅ Changé
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }

    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }

    public double getExpectedSalary() { return expectedSalary; }
    public void setExpectedSalary(double expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
