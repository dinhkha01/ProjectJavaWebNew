package ra.web.dto.admin;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ra.web.entity.RecruitmentPosition;
import ra.web.entity.Technology;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentPositionDto {

    private Integer id;

    @NotBlank(message = "Tên vị trí không được để trống")
    @Size(min = 3, max = 100, message = "Tên vị trí phải từ 3 đến 100 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Lương tối thiểu không được để trống")
    @DecimalMin(value = "0.0", message = "Lương tối thiểu phải >= 0")
    private BigDecimal minSalary;

    @DecimalMin(value = "0.0", message = "Lương tối đa phải >= 0")
    private BigDecimal maxSalary;

    @NotNull(message = "Kinh nghiệm tối thiểu không được để trống")
    @Min(value = 0, message = "Kinh nghiệm tối thiểu phải >= 0")
    private Integer minExperience;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;

    private Boolean isDeleted;

    private Set<Integer> technologyIds; // Dùng để nhận list id của technology từ form

    // Chuyển đổi từ Entity sang DTO
    public RecruitmentPositionDto(RecruitmentPosition position) {
        this.id = position.getId();
        this.name = position.getName();
        this.description = position.getDescription();
        this.minSalary = position.getMinSalary();
        this.maxSalary = position.getMaxSalary();
        this.minExperience = position.getMinExperience();
        this.expiredDate = position.getExpiredDate();
        this.isDeleted = position.getIsDeleted();
        if (position.getTechnologies() != null) {
            this.technologyIds = position.getTechnologies().stream()
                    .map(Technology::getId)
                    .collect(Collectors.toSet());
        }
    }

    // Chuyển đổi từ DTO sang Entity (dùng cho thêm mới và cập nhật)
    public RecruitmentPosition toEntity() {
        RecruitmentPosition position = new RecruitmentPosition();
        position.setId(this.id);
        position.setName(this.name);
        position.setDescription(this.description);
        position.setMinSalary(this.minSalary);
        position.setMaxSalary(this.maxSalary);
        position.setMinExperience(this.minExperience);
        position.setExpiredDate(this.expiredDate);
        position.setIsDeleted(this.isDeleted != null ? this.isDeleted : false);
        return position;
    }
}