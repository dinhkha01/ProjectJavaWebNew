package ra.web.service.admin;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.admin.IApplicationDao;
import ra.web.dto.PageDto;
import ra.web.dto.admin.ApplicationDto;
import ra.web.entity.Application;
import ra.web.entity.Application.ProgressStatus;

import java.time.LocalDateTime;

@Service
public class ApplicationService {

    @Autowired
    private IApplicationDao applicationDao;

    public PageDto<ApplicationDto> findAllWithPagination(int page, int size, String sortBy, String direction,
                                                         ProgressStatus progress, String interviewResult) {
        return applicationDao.findAllWithPagination(page, size, sortBy, direction, progress, interviewResult);
    }

    @Transactional(readOnly = true)
    public Application findById(Integer id) {
        Application application = applicationDao.findById(id);
        if (application != null) {
            // Khởi tạo dữ liệu lazy một cách an toàn
            try {
                Hibernate.initialize(application.getCandidate());
                Hibernate.initialize(application.getRecruitmentPosition());

                // Đảm bảo các thuộc tính cần thiết được load
                if (application.getCandidate() != null) {
                    // Force load candidate properties
                    application.getCandidate().getName();
                    application.getCandidate().getEmail();
                    application.getCandidate().getPhone();
                }

                if (application.getRecruitmentPosition() != null) {
                    // Force load position properties
                    application.getRecruitmentPosition().getName();
                }

            } catch (Exception e) {
                System.err.println("Error initializing lazy properties: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return application;
    }
    @Transactional
    public boolean cancelApplication(Integer id, String reason) {
        return applicationDao.cancelApplication(id, reason);
    }
    @Transactional
    public boolean viewApplication(Integer id) {
      return applicationDao.updateToHandling(id);
    }
    @Transactional
    public boolean moveToInterviewing(Integer id, LocalDateTime interviewRequestDate,
                                      String interviewLink, LocalDateTime interviewTime) {
        return applicationDao.moveToInterviewing(id, interviewRequestDate, interviewLink, interviewTime);
    }
    @Transactional
    public boolean updateInterviewResult(Integer id, String result, String note) {
        return applicationDao.updateInterviewResult(id, result, note);
    }
}