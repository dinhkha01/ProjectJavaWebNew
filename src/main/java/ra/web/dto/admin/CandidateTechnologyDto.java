package ra.web.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CandidateTechnologyDto {
    private Integer candidateId;
    private Set<Integer> technologyIds;
}