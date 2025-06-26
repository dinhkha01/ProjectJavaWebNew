package ra.web.dao.admin;

import org.springframework.stereotype.Repository;
import ra.web.entity.RecruitmentPosition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RecruitmentPositionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RecruitmentPosition> findAllByPage(int page, int size, String sortBy, String direction, String keyword) {
        if ("id".equals(sortBy)) {
            sortBy = "id";
        }
        String jpql = "SELECT DISTINCT rp FROM RecruitmentPosition rp " +
                "LEFT JOIN FETCH rp.technologies " +
                "WHERE rp.name LIKE :keyword AND NOT rp.name LIKE '%_deleted' " +
                "ORDER BY rp." + sortBy + " " + direction;

        TypedQuery<RecruitmentPosition> query = entityManager.createQuery(jpql, RecruitmentPosition.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countAll(String keyword) {
        String jpql = "SELECT COUNT(rp) FROM RecruitmentPosition rp WHERE rp.name LIKE :keyword AND NOT rp.name LIKE '%_deleted'";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getSingleResult();
    }

    public RecruitmentPosition findById(Integer id) {
        return entityManager.find(RecruitmentPosition.class, id);
    }

    public RecruitmentPosition save(RecruitmentPosition position) {
        if (position.getId() == null) {
            entityManager.persist(position);
            return position;
        } else {
            return entityManager.merge(position);
        }
    }

    public void softDelete(Integer id) {
        RecruitmentPosition position = findById(id);
        if (position != null) {
            position.setIsDeleted(true);
            position.setName(position.getName() + "_deleted");
            entityManager.merge(position);
        }
    }

    public long countAllPositions() {
        String jpql = "SELECT COUNT(rp) FROM RecruitmentPosition rp WHERE NOT rp.name LIKE '%_deleted'";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    public long countActivePositions() {
        String jpql = "SELECT COUNT(rp) FROM RecruitmentPosition rp WHERE rp.isDeleted = false AND NOT rp.name LIKE '%_deleted'";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    public long countDeletedPositions() {
        String jpql = "SELECT COUNT(rp) FROM RecruitmentPosition rp WHERE rp.isDeleted = true OR rp.name LIKE '%_deleted'";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    public LocalDateTime getLastUpdate() {
        String jpql = "SELECT MAX(rp.lastUpdated) FROM RecruitmentPosition rp";
        TypedQuery<LocalDateTime> query = entityManager.createQuery(jpql, LocalDateTime.class);
        return query.getSingleResult();
    }
}