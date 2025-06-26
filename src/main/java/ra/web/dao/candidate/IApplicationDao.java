// IApplicationDao.java
package ra.web.dao.candidate;

import ra.web.dto.PageDto;
import ra.web.entity.Application;

public interface IApplicationDao {
    PageDto<Application> findByCandidateId(Integer candidateId, int page, int size);
    Application findByIdAndCandidateId(Integer id, Integer candidateId);
    void updateInterviewConfirmation(Integer id, boolean isConfirmed, String candidateResponse);
}