package ra.web.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "experience", columnDefinition = "INT DEFAULT 0")
    private Integer experience = 0;

    @Column(name = "gender", length = 10)
    @Pattern(regexp = "Nam|Nữ", message = "Giới tính chỉ có thể là Nam hoặc Nữ")
    private String gender;

    @Column(name = "status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "is_deleted", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isDeleted = false;

    // Quan hệ nhiều-nhiều với Technology
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "candidate_technology",
            joinColumns = @JoinColumn(name = "candidateId"),
            inverseJoinColumns = @JoinColumn(name = "technologyId")
    )
    private Set<Technology> technologies;

    // Quan hệ một-nhiều với Application
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Application> applications;
}