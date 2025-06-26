package ra.web.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidateId", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitmentPositionId", nullable = false)
    private RecruitmentPosition recruitmentPosition;

    @Column(name = "cvUrl", nullable = false, length = 255)
    @NotBlank(message = "Đường dẫn CV không được để trống")
    private String cvUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress", nullable = false, columnDefinition = "ENUM('pending', 'handling', 'interviewing', 'done') DEFAULT 'pending'")
    private ProgressStatus progress = ProgressStatus.pending;

    @Column(name = "interviewRequestDate")
    private LocalDateTime interviewRequestDate;

    @Column(name = "interviewRequestResult", length = 100)
    private String interviewRequestResult;

    @Column(name = "interviewLink", length = 255)
    private String interviewLink;

    @Column(name = "interviewTime")
    private LocalDateTime interviewTime;

    @Column(name = "interviewResult", length = 50)
    private String interviewResult;

    @Column(name = "interviewResultNote", columnDefinition = "TEXT")
    private String interviewResultNote;

    @Column(name = "destroyAt")
    private LocalDateTime destroyAt;

    @Column(name = "createAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "updateAt", columnDefinition = "DATETIME ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateAt;

    @Column(name = "destroyReason", columnDefinition = "TEXT")
    private String destroyReason;

    @PreUpdate
    private void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        if (this.createAt == null) {
            this.createAt = LocalDateTime.now();
        }
    }

    // Enum cho trạng thái đơn ứng tuyển
    public enum ProgressStatus {
        pending, handling, interviewing, done
    }
}