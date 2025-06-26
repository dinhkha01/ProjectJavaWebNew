package ra.web.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaginationDao {
    @PersistenceContext
    private EntityManager entityManager;
//    public List<User> getUsersByPage(int page, int size, String sortBy, String direction, String keyword) {
//       String query = "from User where fullName like :keyword or username like :keyword order by "+sortBy+" "+direction;
//       return entityManager.createQuery(query, User.class)
//               .setParameter("keyword", "%" + keyword + "%")
//                .setFirstResult(page*size)
//                .setMaxResults(size)
//                .getResultList();
//    }
//
//    public long totalPages(int size, String keyword){
//           String sql = "select ceiling(count(u)/:size) from User u where u.fullName like :keyword or u.username like :keyword";
//           return entityManager.createQuery(sql, Integer.class)
//                   .setParameter("size", Long.valueOf(size))
//                   .setParameter("keyword", "%" + keyword + "%")
//                   .getSingleResult();
//    }
}
