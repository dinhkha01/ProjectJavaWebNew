package ra.web.dao.candidate;

import org.springframework.stereotype.Repository;
import ra.web.entity.RecruitmentPosition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CandidateRecruitmentPositionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RecruitmentPosition> findActivePositions(int page, int size, String keyword) {
        String jpql = "SELECT rp FROM RecruitmentPosition rp " +
                "LEFT JOIN FETCH rp.technologies " +
                "WHERE rp.isDeleted = false " +
                "AND (rp.name LIKE :keyword OR rp.description LIKE :keyword) " +
                "AND (rp.expiredDate IS NULL OR rp.expiredDate >= :currentDate) " +
                "ORDER BY rp.id DESC";

        TypedQuery<RecruitmentPosition> query = entityManager.createQuery(jpql, RecruitmentPosition.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("currentDate", LocalDate.now());
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countActivePositions(String keyword) {
        String jpql = "SELECT COUNT(rp) FROM RecruitmentPosition rp " +
                "WHERE rp.isDeleted = false " +
                "AND (rp.name LIKE :keyword OR rp.description LIKE :keyword) " +
                "AND (rp.expiredDate IS NULL OR rp.expiredDate >= :currentDate)";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("currentDate", LocalDate.now());
        return query.getSingleResult();
    }

    public RecruitmentPosition findById(Integer id) {
        String jpql = "SELECT DISTINCT rp FROM RecruitmentPosition rp " +
                "LEFT JOIN FETCH rp.technologies " +
                "WHERE rp.id = :id " +
                "AND (rp.expiredDate IS NULL OR rp.expiredDate >= :currentDate)";

        TypedQuery<RecruitmentPosition> query = entityManager.createQuery(jpql, RecruitmentPosition.class);
        query.setParameter("id", id);
        query.setParameter("currentDate", LocalDate.now());

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}