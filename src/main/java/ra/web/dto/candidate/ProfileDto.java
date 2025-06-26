package ra.web.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ra.web.entity.Candidate;
import ra.web.entity.Technology;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải từ 10-11 chữ số")
    private String phone;

    @Min(value = 0, message = "Kinh nghiệm không được âm")
    @Max(value = 50, message = "Kinh nghiệm không được quá 50 năm")
    private Integer experience;

    @Pattern(regexp = "Nam|Nữ", message = "Giới tính chỉ có thể là Nam hoặc Nữ")
    private String gender;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String status;
    private Boolean isDeleted;

    // Danh sách tên công nghệ
    private Set<String> technologyNames;

    // Constructor từ Entity
    public ProfileDto(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.email = candidate.getEmail();
        this.phone = candidate.getPhone();
        this.experience = candidate.getExperience();
        this.gender = candidate.getGender();
        this.description = candidate.getDescription();
        this.dob = candidate.getDob();
        this.status = candidate.getStatus();
        this.isDeleted = candidate.getIsDeleted();

        // Chuyển đổi technologies
        if (candidate.getTechnologies() != null) {
            this.technologyNames = candidate.getTechnologies().stream()
                    .map(Technology::getName)
                    .collect(Collectors.toSet());
        }
    }

    // Chuyển đổi sang Entity (không bao gồm technologies)
    public Candidate toEntity() {
        Candidate candidate = new Candidate();
        candidate.setId(this.id);
        candidate.setName(this.name);
        candidate.setEmail(this.email);
        candidate.setPhone(this.phone);
        candidate.setExperience(this.experience != null ? this.experience : 0);
        candidate.setGender(this.gender);
        candidate.setDescription(this.description);
        candidate.setDob(this.dob);
        candidate.setStatus(this.status != null ? this.status : "active");
        candidate.setIsDeleted(this.isDeleted != null ? this.isDeleted : false);
        return candidate;
    }
}