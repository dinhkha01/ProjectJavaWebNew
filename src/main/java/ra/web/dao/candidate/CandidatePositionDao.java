package ra.web.dao.candidate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.web.entity.Application;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class CandidatePositionDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public void save(Application application) {
        entityManager.persist(application);
    }

    public boolean existsByCandidateAndPosition(Integer candidateId, Integer positionId) {
        String jpql = "SELECT COUNT(a) FROM Application a " +
                "WHERE a.candidate.id = :candidateId AND a.recruitmentPosition.id = :positionId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("candidateId", candidateId);
        query.setParameter("positionId", positionId);
        return query.getSingleResult() > 0;
    }
}