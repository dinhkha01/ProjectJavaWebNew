package ra.web.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.admin.ICandidateDao;
import ra.web.dto.PageDto;
import ra.web.dto.admin.CandidateDto;
import ra.web.entity.Candidate;

import java.util.Set;

@Service
@Transactional
public class CandidateService {

    @Autowired
    private ICandidateDao candidateDao;

    public PageDto<CandidateDto> findAllWithPagination(int page, int size, String keyword,
                                                       String sortBy, String direction,
                                                       Integer minExperience, Integer maxExperience,
                                                       Integer minAge, Integer maxAge,
                                                       String gender, String technology) {
        return candidateDao.findAllWithPagination(page, size, keyword, sortBy, direction,
                minExperience, maxExperience, minAge, maxAge, gender, technology);
    }

    public Candidate findById(Integer id) {
        return candidateDao.findById(id);
    }

    public boolean toggleStatus(Integer id) {
        Candidate candidate = candidateDao.findById(id);
        if (candidate != null) {
            String newStatus = "active".equals(candidate.getStatus()) ? "inactive" : "active";
            return candidateDao.updateStatus(id, newStatus);
        }
        return false;
    }
    public void addTechnologiesToCandidate(Integer candidateId, Set<Integer> technologyIds) {
        candidateDao.addTechnologiesToCandidate(candidateId, technologyIds);
    }

    public Set<String> getCandidateTechnologies(Integer candidateId) {
        return candidateDao.getCandidateTechnologies(candidateId);
    }

    public long getTotalCandidates() {
        return candidateDao.countTotal();
    }

    public long getActiveCandidates() {
        return candidateDao.countByStatus("active");
    }

    public long getInactiveCandidates() {
        return candidateDao.countByStatus("inactive");
    }

    public long getDeletedCandidates() {
        return candidateDao.countDeleted();
    }
}