// CandidateApplicationDao.java
package ra.web.dao.candidate;

import org.springframework.stereotype.Repository;
import ra.web.dto.PageDto;
import ra.web.entity.Application;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CandidateApplicationDao implements IApplicationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageDto<Application> findByCandidateId(Integer candidateId, int page, int size) {
        // Count query (giữ nguyên)
        String countQueryStr = "SELECT COUNT(a) FROM Application a WHERE a.candidate.id = :candidateId";
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryStr, Long.class);
        countQuery.setParameter("candidateId", candidateId);
        long totalItems = countQuery.getSingleResult();
        long totalPages = (totalItems + size - 1) / size;

        // Sửa query chính để JOIN FETCH dữ liệu quan hệ
        String queryStr = "SELECT a FROM Application a " +
                "LEFT JOIN FETCH a.recruitmentPosition " +
                "WHERE a.candidate.id = :candidateId " +
                "ORDER BY a.createAt DESC";
        TypedQuery<Application> query = entityManager.createQuery(queryStr, Application.class);
        query.setParameter("candidateId", candidateId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<Application> applications = query.getResultList();

        return PageDto.<Application>builder()
                .content(applications)
                .currentPage(page)
                .totalPages(totalPages)
                .size(size)
                .build();
    }

    @Override
    public Application findByIdAndCandidateId(Integer id, Integer candidateId) {
        String queryStr = "SELECT a FROM Application a WHERE a.id = :id AND a.candidate.id = :candidateId";
        TypedQuery<Application> query = entityManager.createQuery(queryStr, Application.class);
        query.setParameter("id", id);
        query.setParameter("candidateId", candidateId);
        return query.getSingleResult();
    }

    @Override
    public void updateInterviewConfirmation(Integer id, boolean isConfirmed, String candidateResponse) {
        Application application = entityManager.find(Application.class, id);
        if (application != null) {
            application.setInterviewRequestResult(isConfirmed ? "Confirmed" : "Rejected");
            application.setInterviewResultNote(candidateResponse);
            entityManager.merge(application);
        }
    }
}