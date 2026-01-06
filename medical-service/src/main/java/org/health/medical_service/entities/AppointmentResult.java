package org.health.medical_service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "appointmentResult"
)
public class AppointmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 500)
    private String summary; // Short, clear summary of the appointment outcome

    @Column(length = 5000)
    private String detailedNotes; // Detailed notes from doctor, observations, test results

    @Column(nullable = false)
    private boolean followUpRecommended; // Flag for whether follow-up is needed

    @Column(length = 500)
    private String followUpInstructions; // Recommended next steps for patient

    @Column(length = 1000)
    private String prescriptions; // Medicines or therapies prescribed

    @Column(length = 1000)
    private String labTests; // Any tests ordered or results noted

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AppointmentResult() {}

    public AppointmentResult(UUID id, String summary, String detailedNotes,
                             boolean followUpRecommended, String followUpInstructions,
                             String prescriptions, String labTests, Appointment appointment) {
        this.id = id;
        this.summary = summary;
        this.detailedNotes = detailedNotes;
        this.followUpRecommended = followUpRecommended;
        this.followUpInstructions = followUpInstructions;
        this.prescriptions = prescriptions;
        this.labTests = labTests;
        this.appointment = appointment;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDetailedNotes() { return detailedNotes; }
    public void setDetailedNotes(String detailedNotes) { this.detailedNotes = detailedNotes; }

    public boolean isFollowUpRecommended() { return followUpRecommended; }
    public void setFollowUpRecommended(boolean followUpRecommended) { this.followUpRecommended = followUpRecommended; }

    public String getFollowUpInstructions() { return followUpInstructions; }
    public void setFollowUpInstructions(String followUpInstructions) { this.followUpInstructions = followUpInstructions; }

    public String getPrescriptions() { return prescriptions; }
    public void setPrescriptions(String prescriptions) { this.prescriptions = prescriptions; }

    public String getLabTests() { return labTests; }
    public void setLabTests(String labTests) { this.labTests = labTests; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
