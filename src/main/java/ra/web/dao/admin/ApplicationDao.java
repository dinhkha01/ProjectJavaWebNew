package ra.web.dao.admin;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dto.PageDto;
import ra.web.dto.admin.ApplicationDto;
import ra.web.entity.Application;
import ra.web.entity.Application.ProgressStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ApplicationDao implements IApplicationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageDto<ApplicationDto> findAllWithPagination(int page, int size, String sortBy, String direction,
                                                         ProgressStatus progress, String interviewResult) {
        // Build JPQL query với JOIN FETCH để load luôn candidate và position
        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Application a " +
                        "JOIN FETCH a.candidate c " +
                        "JOIN FETCH a.recruitmentPosition rp " +
                        "WHERE a.destroyAt IS NULL"
        );

        // Add progress filter
        if (progress != null) {
            jpql.append(" AND a.progress = :progress");
        }

        // Add interview result filter
        if (interviewResult != null && !interviewResult.isEmpty()) {
            jpql.append(" AND a.interviewResult = :interviewResult");
        }

        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            jpql.append(" ORDER BY a.").append(sortBy);
            jpql.append(" ").append(direction.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        } else {
            jpql.append(" ORDER BY a.id DESC");
        }

        // Create query for data
        TypedQuery<Application> query = entityManager.createQuery(jpql.toString(), Application.class);

        // Set parameters
        if (progress != null) {
            query.setParameter("progress", progress);
        }
        if (interviewResult != null && !interviewResult.isEmpty()) {
            query.setParameter("interviewResult", interviewResult);
        }

        // Apply pagination
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Application> applications = query.getResultList();

        // Convert to DTO
        List<ApplicationDto> applicationDtos = applications.stream()
                .map(ApplicationDto::new)
                .collect(Collectors.toList());

        // Create count query (không cần JOIN FETCH cho count)
        String countJpql = "SELECT COUNT(a.id) FROM Application a WHERE a.destroyAt IS NULL";

        if (progress != null) {
            countJpql += " AND a.progress = :progress";
        }
        if (interviewResult != null && !interviewResult.isEmpty()) {
            countJpql += " AND a.interviewResult = :interviewResult";
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);

        // Set parameters for count query
        if (progress != null) {
            countQuery.setParameter("progress", progress);
        }
        if (interviewResult != null && !interviewResult.isEmpty()) {
            countQuery.setParameter("interviewResult", interviewResult);
        }

        Long totalElements = countQuery.getSingleResult();
        long totalPages = (totalElements + size - 1) / size;

        return PageDto.<ApplicationDto>builder()
                .content(applicationDtos)
                .currentPage(page)
                .totalPages(totalPages)
                .size(size)
                .build();
    }

    @Override
    public Application findById(Integer id) {
        return entityManager.find(Application.class,id);
    }

    @Override
    public boolean cancelApplication(Integer id, String reason) {
        Application application = findById(id);
        if (application != null && application.getDestroyAt() == null) {
            application.setDestroyAt(LocalDateTime.now());
            application.setDestroyReason(reason);
            entityManager.merge(application);
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    public boolean updateToHandling(Integer id) {
        Application application = findById(id);
        if (application != null && application.getProgress() == ProgressStatus.pending) {
            application.setProgress(ProgressStatus.handling);
            entityManager.merge(application);
            return true;
        }
        return false;
    }
    @Override
    public boolean moveToInterviewing(Integer id, LocalDateTime interviewRequestDate,
                                      String interviewLink, LocalDateTime interviewTime) {
        Application application = findById(id);
        if (application != null && application.getProgress() == ProgressStatus.handling) {
            application.setProgress(ProgressStatus.interviewing);
            application.setInterviewRequestDate(interviewRequestDate);
            application.setInterviewLink(interviewLink);
            application.setInterviewTime(interviewTime);
            entityManager.merge(application);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateInterviewResult(Integer id, String result, String note) {
        Application application = findById(id);
        if (application != null && application.getProgress() == ProgressStatus.interviewing) {
            application.setProgress(ProgressStatus.done);
            application.setInterviewResult(result);
            application.setInterviewResultNote(note);
            entityManager.merge(application);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateApplication(Application application) {
        entityManager.merge(application);
        return true;
    }
}