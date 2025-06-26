package ra.web.dto.candidate;

import lombok.Getter;
import lombok.Setter;
import ra.web.entity.RecruitmentPosition;
import ra.web.entity.Technology;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class CandidateRecruitmentPositionDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Integer minExperience;
    private LocalDate expiredDate;
    private Set<String> technologyNames;
    private boolean applied;

    public CandidateRecruitmentPositionDto(RecruitmentPosition position, boolean applied) {
        this.id = position.getId();
        this.name = position.getName();
        this.description = position.getDescription();
        this.minSalary = position.getMinSalary();
        this.maxSalary = position.getMaxSalary();
        this.minExperience = position.getMinExperience();
        this.expiredDate = position.getExpiredDate();

        // Kiểm tra null trước khi sử dụng stream
        this.technologyNames = new HashSet<>();
        if (position.getTechnologies() != null) {
            for (Technology tech : position.getTechnologies()) {
                this.technologyNames.add(tech.getName());
            }
        }

        this.applied = applied;
    }
}