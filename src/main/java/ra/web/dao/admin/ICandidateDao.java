
package ra.web.dao.admin;

import ra.web.dto.PageDto;
import ra.web.dto.admin.CandidateDto;
import ra.web.entity.Candidate;

import java.util.Set;

public interface ICandidateDao {
    PageDto<CandidateDto> findAllWithPagination(int page, int size, String keyword,
                                                String sortBy, String direction,
                                                Integer minExperience, Integer maxExperience,
                                                Integer minAge, Integer maxAge,
                                                String gender, String technology);

    Candidate findById(Integer id);
    void addTechnologiesToCandidate(Integer candidateId, Set<Integer> technologyIds);
    Set<String> getCandidateTechnologies(Integer candidateId);

    boolean updateStatus(Integer id, String status);

    long countTotal();

    long countByStatus(String status);

    long countDeleted();
}