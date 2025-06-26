package ra.web.dto.admin;

import lombok.*;
import ra.web.entity.Application;
import ra.web.entity.Application.ProgressStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Integer id;
    private Integer candidateId;
    private String candidateName;
    private Integer positionId;
    private String positionName;
    private String cvUrl;
    private ProgressStatus progress;
    private LocalDateTime interviewRequestDate;
    private String interviewRequestResult;
    private String interviewLink;
    private LocalDateTime interviewTime;
    private String interviewResult;
    private String interviewResultNote;
    private LocalDateTime destroyAt;
    private String destroyReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ApplicationDto(Application application) {
        this.id = application.getId();
        this.candidateId = application.getCandidate().getId();
        this.candidateName = application.getCandidate().getName();
        this.positionId = application.getRecruitmentPosition().getId();
        this.positionName = application.getRecruitmentPosition().getName();
        this.cvUrl = application.getCvUrl();
        this.progress = application.getProgress();
        this.interviewRequestDate = application.getInterviewRequestDate();
        this.interviewRequestResult = application.getInterviewRequestResult();
        this.interviewLink = application.getInterviewLink();
        this.interviewTime = application.getInterviewTime();
        this.interviewResult = application.getInterviewResult();
        this.interviewResultNote = application.getInterviewResultNote();
        this.destroyAt = application.getDestroyAt();
        this.destroyReason = application.getDestroyReason();
        this.createAt = application.getCreateAt();
        this.updateAt = application.getUpdateAt();
    }
}