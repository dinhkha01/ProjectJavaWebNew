// CandidateApplicationService.java
package ra.web.service.candidate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.candidate.IApplicationDao;
import ra.web.dto.PageDto;
import ra.web.dto.candidate.CandidateApplicationDto;
import ra.web.entity.Application;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateApplicationService {
    private final IApplicationDao applicationDao;
    @Transactional(readOnly = true)
    public PageDto<CandidateApplicationDto> getApplications(Integer candidateId, int page, int size) {
        PageDto<Application> applicationPage = applicationDao.findByCandidateId(candidateId, page, size);

        List<CandidateApplicationDto> content = applicationPage.getContent().stream()
                .map(CandidateApplicationDto::fromEntity)
                .collect(Collectors.toList());

        return PageDto.<CandidateApplicationDto>builder()
                .content(content)
                .currentPage(applicationPage.getCurrentPage())
                .totalPages(applicationPage.getTotalPages())
                .size(applicationPage.getSize())
                .build();
    }

    public CandidateApplicationDto getApplicationDetails(Integer applicationId, Integer candidateId) {
        Application application = applicationDao.findByIdAndCandidateId(applicationId, candidateId);
        return CandidateApplicationDto.fromEntity(application);
    }

    public void confirmInterview(Integer applicationId, Integer candidateId, boolean isConfirmed, String response) {
        applicationDao.updateInterviewConfirmation(applicationId, isConfirmed, response);
    }
}