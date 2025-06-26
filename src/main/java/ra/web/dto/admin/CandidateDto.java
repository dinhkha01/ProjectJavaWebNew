package ra.web.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.web.entity.Candidate;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Integer experience;
    private String gender;
    private String status;
    private String description;
    private LocalDate dob;
    private Boolean isDeleted;
    private Set<String> technologyNames;
    private Integer age;

    // Constructor cho việc chuyển đổi từ Entity
    public CandidateDto(Integer id, String name, String email, String phone,
                        Integer experience, String gender, String status,
                        String description, LocalDate dob, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.experience = experience;
        this.gender = gender;
        this.status = status;
        this.description = description;
        this.dob = dob;
        this.isDeleted = isDeleted;

        // Tính tuổi từ ngày sinh
        if (dob != null) {
            this.age = LocalDate.now().getYear() - dob.getYear();
        }
    }
    private CandidateDto convertToDto(Candidate candidate) {
        CandidateDto dto = new CandidateDto(
                candidate.getId(),
                candidate.getName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getExperience(),
                candidate.getGender(),
                candidate.getStatus(),
                candidate.getDescription(),
                candidate.getDob(),
                candidate.getIsDeleted()
        );

        // Set technology names
        if (candidate.getTechnologies() != null) {
            Set<String> techNames = candidate.getTechnologies().stream()
                    .map(tech -> tech.getName())
                    .collect(Collectors.toSet());
            dto.setTechnologyNames(techNames);
        }

        return dto;
    }
}