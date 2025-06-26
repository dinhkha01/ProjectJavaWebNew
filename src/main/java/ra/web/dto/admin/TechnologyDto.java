package ra.web.dto.admin;

import lombok.*;
import ra.web.entity.Technology;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnologyDto {

    private Integer id;

    @NotBlank(message = "Tên công nghệ không được để trống")
    @Size(min = 1, max = 100, message = "Tên công nghệ phải từ 1 đến 100 ký tự")
    private String name;

    private Boolean isDeleted = false;

    // Constructor chuyển đổi từ Entity
    public TechnologyDto(Technology technology) {
        this.id = technology.getId();
        this.name = technology.getName();
        this.isDeleted = technology.getIsDeleted();
    }

    // Chuyển đổi sang Entity
    public Technology toEntity() {
        Technology technology = new Technology();
        technology.setId(this.id);
        technology.setName(this.name);
        technology.setIsDeleted(this.isDeleted != null ? this.isDeleted : false);
        return technology;
    }
}