package ra.web.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "recruitment_position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Tên vị trí không được để trống")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "minSalary", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Lương tối thiểu phải >= 0")
    private BigDecimal minSalary;

    @Column(name = "maxSalary", precision = 12, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "minExperience", columnDefinition = "INT DEFAULT 0")
    private Integer minExperience = 0;

    @Column(name = "createdDate")
    private LocalDate createdDate;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "expiredDate", nullable = false)
    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDate expiredDate;

    @Column(name = "is_deleted", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isDeleted = false;

    // Quan hệ nhiều-nhiều với Technology
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recruitment_position_technology",
            joinColumns = @JoinColumn(name = "recruitmentPositionId"),
            inverseJoinColumns = @JoinColumn(name = "technologyId")
    )
    private Set<Technology> technologies;

    // Quan hệ một-nhiều với Application
    @OneToMany(mappedBy = "recruitmentPosition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Application> applications;

    @PrePersist
    private void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();
        }
        validateSalary();
    }

    @PreUpdate
    private void validateSalary() {
        if (minSalary != null && maxSalary != null && maxSalary.compareTo(minSalary) < 0) {
            throw new IllegalArgumentException("Lương tối đa phải >= lương tối thiểu");
        }
    }
}