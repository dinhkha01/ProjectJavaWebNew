package ra.web.dao.admin;

import org.springframework.stereotype.Repository;
import ra.web.dto.PageDto;
import ra.web.dto.admin.CandidateDto;
import ra.web.entity.Candidate;
import ra.web.entity.Technology;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CandidateDao implements ICandidateDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageDto<CandidateDto> findAllWithPagination(int page, int size, String keyword,
                                                       String sortBy, String direction,
                                                       Integer minExperience, Integer maxExperience,
                                                       Integer minAge, Integer maxAge,
                                                       String gender, String technology) {
        // Build JPQL query with filtering conditions
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT c FROM Candidate c LEFT JOIN FETCH c.technologies t WHERE c.isDeleted = false");

        // Add search by name condition
        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND LOWER(c.name) LIKE LOWER(:keyword)");
        }

        // Filter by experience
        if (minExperience != null) {
            jpql.append(" AND c.experience >= :minExperience");
        }
        if (maxExperience != null) {
            jpql.append(" AND c.experience <= :maxExperience");
        }

        // Filter by age
        if (minAge != null) {
            jpql.append(" AND YEAR(CURRENT_DATE) - YEAR(c.dob) >= :minAge");
        }
        if (maxAge != null) {
            jpql.append(" AND YEAR(CURRENT_DATE) - YEAR(c.dob) <= :maxAge");
        }

        // Filter by gender
        if (gender != null && !gender.trim().isEmpty() && !gender.equals("all")) {
            jpql.append(" AND c.gender = :gender");
        }

        // Filter by technology
        if (technology != null && !technology.trim().isEmpty() && !technology.equals("all")) {
            jpql.append(" AND EXISTS (SELECT 1 FROM c.technologies tech WHERE tech.name = :technology)");
        }

        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            jpql.append(" ORDER BY c.").append(sortBy);
            jpql.append(" ").append(direction.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        } else {
            jpql.append(" ORDER BY c.id DESC");
        }

        // Create query for data
        TypedQuery<Candidate> query = entityManager.createQuery(jpql.toString(), Candidate.class);

        // Set parameters
        setQueryParameters(query, keyword, minExperience, maxExperience,
                minAge, maxAge, gender, technology);

        // Apply pagination
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Candidate> candidates = query.getResultList();

        // Convert to DTO
        List<CandidateDto> candidateDtos = candidates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Create count query
        String countJpql = jpql.toString()
                .replace("SELECT DISTINCT c FROM Candidate c LEFT JOIN FETCH c.technologies t", "SELECT COUNT(DISTINCT c.id) FROM Candidate c LEFT JOIN c.technologies t")
                .replace("ORDER BY c.id DESC", "")
                .replace("ORDER BY c." + sortBy + " " + (direction.equalsIgnoreCase("desc") ? "DESC" : "ASC"), "");

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        setQueryParameters(countQuery, keyword, minExperience, maxExperience,
                minAge, maxAge, gender, technology);

        Long totalElements = countQuery.getSingleResult();
        long totalPages = (totalElements + size - 1) / size;

        return PageDto.<CandidateDto>builder()
                .content(candidateDtos)
                .currentPage(page)
                .totalPages(totalPages)
                .size(size)
                .keyword(keyword)
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    private void setQueryParameters(TypedQuery<?> query, String keyword, Integer minExperience,
                                    Integer maxExperience, Integer minAge, Integer maxAge,
                                    String gender, String technology) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim() + "%");
        }
        if (minExperience != null) {
            query.setParameter("minExperience", minExperience);
        }
        if (maxExperience != null) {
            query.setParameter("maxExperience", maxExperience);
        }
        if (minAge != null) {
            query.setParameter("minAge", minAge);
        }
        if (maxAge != null) {
            query.setParameter("maxAge", maxAge);
        }
        if (gender != null && !gender.trim().isEmpty() && !gender.equals("all")) {
            query.setParameter("gender", gender);
        }
        if (technology != null && !technology.trim().isEmpty() && !technology.equals("all")) {
            query.setParameter("technology", technology);
        }
    }

    private CandidateDto convertToDto(Candidate candidate) {
        CandidateDto dto = new CandidateDto(
                candidate.getId(),
                candidate.getName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getExperience(),
                candidate.getGender(),
                candidate.getStatus(),
                candidate.getDescription(),
                candidate.getDob(),
                candidate.getIsDeleted()
        );

        // Set technology names
        if (candidate.getTechnologies() != null) {
            Set<String> techNames = candidate.getTechnologies().stream()
                    .map(tech -> tech.getName())
                    .collect(Collectors.toSet());
            dto.setTechnologyNames(techNames);
        }

        return dto;
    }

    @Override
    public Candidate findById(Integer id) {
        return entityManager.find(Candidate.class, id);
    }

    @Override
    public boolean updateStatus(Integer id, String status) {
        Candidate candidate = findById(id);
        if (candidate != null) {
            candidate.setStatus(status);
            entityManager.merge(candidate);
            return true;
        }
        return false;
    }
    @Override
    public void addTechnologiesToCandidate(Integer candidateId, Set<Integer> technologyIds) {
        Candidate candidate = entityManager.find(Candidate.class, candidateId);
        if (candidate != null) {
            // Lấy danh sách công nghệ từ IDs
            TypedQuery<Technology> query = entityManager.createQuery(
                    "SELECT t FROM Technology t WHERE t.id IN :ids AND t.isDeleted = false",
                    Technology.class
            );
            query.setParameter("ids", technologyIds);
            Set<Technology> technologies = new HashSet<>(query.getResultList());

            // Thêm vào candidate
            candidate.getTechnologies().addAll(technologies);
            entityManager.merge(candidate);
        }
    }

    @Override
    public Set<String> getCandidateTechnologies(Integer candidateId) {
        Candidate candidate = entityManager.find(Candidate.class, candidateId);
        if (candidate != null && candidate.getTechnologies() != null) {
            return candidate.getTechnologies().stream()
                    .map(Technology::getName)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public long countTotal() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Candidate c WHERE c.isDeleted = false", Long.class);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(String status) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Candidate c WHERE c.status = :status AND c.isDeleted = false", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countDeleted() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Candidate c WHERE c.isDeleted = true", Long.class);
        return query.getSingleResult();
    }
}