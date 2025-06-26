package ra.web.dao.admin;

import org.springframework.stereotype.Repository;
import ra.web.entity.Technology;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class TechnologyDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Lấy danh sách công nghệ với phân trang (không lấy những công nghệ có tên kết thúc bằng *deleted)
    public List<Technology> getTechnologiesByPage(int page, int size, String sortBy, String direction, String keyword) {
        String jpql = "SELECT t FROM Technology t WHERE (t.name LIKE :keyword) AND (t.name NOT LIKE '%_deleted') ORDER BY t." + sortBy + " " + direction;

        TypedQuery<Technology> query = entityManager.createQuery(jpql, Technology.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    // Đếm tổng số trang
    public long getTotalPages(int size, String keyword) {
        String jpql = "SELECT COUNT(t) FROM Technology t WHERE (t.name LIKE :keyword) AND (t.name NOT LIKE '%_deleted')";

        TypedQuery<Long> countQuery = entityManager.createQuery(jpql, Long.class);
        countQuery.setParameter("keyword", "%" + keyword + "%");
        long count = countQuery.getSingleResult();

        // Calcul manuel de la pagination
        return (long) Math.ceil((double) count / size);
    }

    // Lấy tất cả công nghệ (không phân trang)
    public List<Technology> getAllTechnologies() {
        String jpql = "SELECT t FROM Technology t WHERE t.name NOT LIKE '%_deleted' ORDER BY t.name ASC";
        return entityManager.createQuery(jpql, Technology.class).getResultList();
    }

    // Tìm theo ID
    public Technology findById(Integer id) {
        return entityManager.find(Technology.class, id);
    }

    // Tìm theo tên
    public Technology findByName(String name) {
        String jpql = "SELECT t FROM Technology t WHERE t.name = :name";
        TypedQuery<Technology> query = entityManager.createQuery(jpql, Technology.class);
        query.setParameter("name", name);

        List<Technology> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Lưu (thêm mới hoặc cập nhật)
    public Technology save(Technology technology) {
        if (technology.getId() == null) {
            entityManager.persist(technology);
            return technology;
        } else {
            return entityManager.merge(technology);
        }
    }

    // Xóa (soft delete - đổi tên thành tên_deleted)
    public void softDelete(Integer id) {
        Technology technology = findById(id);
        if (technology != null) {
            String newName = technology.getName() + "_deleted";
            technology.setName(newName);
            technology.setIsDeleted(true);
            entityManager.merge(technology);
        }
    }

    // Kiểm tra tên có tồn tại không (exclude ID hiện tại khi update)
    public boolean existsByNameAndNotId(String name, Integer excludeId) {
        String jpql = "SELECT COUNT(t) FROM Technology t WHERE t.name = :name";
        if (excludeId != null) {
            jpql += " AND t.id != :excludeId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("name", name);
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }

        return query.getSingleResult() > 0;
    }
    public long countAll() {
        String jpql = "SELECT COUNT(t) FROM Technology t WHERE t.name NOT LIKE '%_deleted'";
        Query query = entityManager.createQuery(jpql);
        return (long) query.getSingleResult();
    }

    public long countActive() {
        String jpql = "SELECT COUNT(t) FROM Technology t WHERE t.isDeleted = false AND t.name NOT LIKE '%_deleted'";
        Query query = entityManager.createQuery(jpql);
        return (long) query.getSingleResult();
    }

    public long countDeleted() {
        String jpql = "SELECT COUNT(t) FROM Technology t WHERE t.isDeleted = true OR t.name LIKE '%_deleted'";
        Query query = entityManager.createQuery(jpql);
        return (long) query.getSingleResult();
    }
    public List<Technology> findAllActive() {
        String jpql = "SELECT t FROM Technology t WHERE t.isDeleted = false";
        return entityManager.createQuery(jpql, Technology.class).getResultList();
    }

}