package ra.web.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "technology")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "is_deleted", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isDeleted = false;

    // Quan hệ nhiều-nhiều với Candidate
    @ManyToMany(mappedBy = "technologies", fetch = FetchType.LAZY)
    private Set<Candidate> candidates;

    // Quan hệ nhiều-nhiều với RecruitmentPosition
    @ManyToMany(mappedBy = "technologies", fetch = FetchType.LAZY)
    private Set<RecruitmentPosition> recruitmentPositions;
}