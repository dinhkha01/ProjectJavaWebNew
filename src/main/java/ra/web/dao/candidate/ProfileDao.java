package ra.web.dao.candidate;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import ra.web.entity.Candidate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProfileDao implements IProfileDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Candidate findById(Integer id) {
        // Sử dụng getResultList() thay vì getResultStream()
        List<Candidate> results = entityManager.createQuery(
                        "SELECT c FROM Candidate c LEFT JOIN FETCH c.technologies WHERE c.id = :id AND c.isDeleted = false",
                        Candidate.class)
                .setParameter("id", id)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Candidate findByEmail(String email) {
        TypedQuery<Candidate> query = entityManager.createQuery(
                "SELECT c FROM Candidate c LEFT JOIN FETCH c.technologies WHERE c.email = :email AND c.isDeleted = false",
                Candidate.class);
        query.setParameter("email", email);

        List<Candidate> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public boolean updateProfile(Candidate candidate) {
        try {
            // Lấy candidate hiện tại từ database
            Candidate existingCandidate = entityManager.find(Candidate.class, candidate.getId());
            if (existingCandidate == null) {
                return false;
            }

            // Cập nhật các trường thông tin (không cập nhật password, technologies)
            existingCandidate.setName(candidate.getName());
            existingCandidate.setEmail(candidate.getEmail());
            existingCandidate.setPhone(candidate.getPhone());
            existingCandidate.setExperience(candidate.getExperience());
            existingCandidate.setGender(candidate.getGender());
            existingCandidate.setDescription(candidate.getDescription());
            existingCandidate.setDob(candidate.getDob());

            entityManager.merge(existingCandidate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean changePassword(Integer candidateId, String newPassword) {
        try {
            Candidate candidate = entityManager.find(Candidate.class, candidateId);
            if (candidate == null) {
                return false;
            }

            candidate.setPassword(newPassword);
            entityManager.merge(candidate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkCurrentPassword(Integer candidateId, String currentPassword) {
        try {
            // Lấy candidate từ database
            Candidate candidate = entityManager.find(Candidate.class, candidateId);
            if (candidate == null) {
                return false;
            }

            // So sánh mật khẩu đã mã hóa bằng BCrypt
            return BCrypt.checkpw(currentPassword, candidate.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean existsByEmailAndNotId(String email, Integer excludeId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Candidate c WHERE c.email = :email AND c.id != :excludeId AND c.isDeleted = false",
                    Long.class);
            query.setParameter("email", email);
            query.setParameter("excludeId", excludeId);

            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    @Transactional
    public boolean updateProfileWithTechnologies(Candidate candidate) {
        try {
            // Lấy candidate hiện tại từ database
            Candidate existingCandidate = entityManager.find(Candidate.class, candidate.getId());
            if (existingCandidate == null) {
                return false;
            }

            // Cập nhật các trường thông tin
            existingCandidate.setName(candidate.getName());
            existingCandidate.setEmail(candidate.getEmail());
            existingCandidate.setPhone(candidate.getPhone());
            existingCandidate.setExperience(candidate.getExperience());
            existingCandidate.setGender(candidate.getGender());
            existingCandidate.setDescription(candidate.getDescription());
            existingCandidate.setDob(candidate.getDob());

            // Cập nhật technologies
            existingCandidate.setTechnologies(candidate.getTechnologies());

            entityManager.merge(existingCandidate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}