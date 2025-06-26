
package ra.web.dao;

import org.springframework.stereotype.Repository;
import ra.web.entity.Candidate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class AuthenDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Tìm candidate theo email
    public Candidate findByEmail(String email) {
        try {
            TypedQuery<Candidate> query = entityManager.createQuery(
                    "SELECT c FROM Candidate c WHERE c.email = :email AND c.isDeleted = false",
                    Candidate.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Candidate c WHERE c.email = :email AND c.isDeleted = false",
                Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    // Lưu candidate mới
    public Candidate save(Candidate candidate) {
        if (candidate.getId() == null) {
            entityManager.persist(candidate);
            return candidate;
        } else {
            return entityManager.merge(candidate);
        }
    }

    // Tìm candidate theo ID
    public Candidate findById(Integer id) {
        return entityManager.find(Candidate.class, id);
    }
}