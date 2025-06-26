package ra.web.dao.admin;

import ra.web.dto.PageDto;
import ra.web.dto.admin.ApplicationDto;
import ra.web.entity.Application;
import ra.web.entity.Application.ProgressStatus;

import java.time.LocalDateTime;

public interface IApplicationDao {
    PageDto<ApplicationDto> findAllWithPagination(int page, int size, String sortBy, String direction,
                                                  ProgressStatus progress, String interviewResult);
    Application findById(Integer id);
    boolean cancelApplication(Integer id, String reason);
    boolean updateToHandling(Integer id);
    boolean moveToInterviewing(Integer id, LocalDateTime interviewRequestDate,
                               String interviewLink, LocalDateTime interviewTime);
    boolean updateInterviewResult(Integer id, String result, String note);
    boolean updateApplication(Application application);
}