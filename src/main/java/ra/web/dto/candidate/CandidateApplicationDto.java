// CandidateApplicationDto.java
package ra.web.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.web.entity.Application;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateApplicationDto {
    private Integer id;
    private String positionName;
    private Application.ProgressStatus progress;
    private LocalDateTime createAt;
    private LocalDateTime interviewTime;
    private String interviewLink;
    private LocalDateTime interviewRequestDate;
    private String interviewResult;
    private boolean canConfirmInterview;

    public static CandidateApplicationDto fromEntity(Application application) {
        CandidateApplicationDto dto = new CandidateApplicationDto();
        dto.setId(application.getId());
        dto.setPositionName(application.getRecruitmentPosition().getName());
        dto.setProgress(application.getProgress());
        dto.setCreateAt(application.getCreateAt());
        dto.setInterviewTime(application.getInterviewTime());
        dto.setInterviewLink(application.getInterviewLink());
        dto.setInterviewRequestDate(application.getInterviewRequestDate());
        dto.setInterviewResult(application.getInterviewResult());
        dto.setCanConfirmInterview(
                application.getProgress() == Application.ProgressStatus.interviewing &&
                        application.getInterviewRequestDate() != null
        );
        return dto;
    }
}