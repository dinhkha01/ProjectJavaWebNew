package ra.web.dto.candidate;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ApplicationRequest {
    @NotNull(message = "Vị trí tuyển dụng không được để trống")
    private Integer positionId;

    @NotBlank(message = "URL CV không được để trống")
    private String cvUrl;
}