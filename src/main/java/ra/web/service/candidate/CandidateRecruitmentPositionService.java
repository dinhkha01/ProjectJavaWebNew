package ra.web.service.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.web.dao.candidate.CandidatePositionDao;
import ra.web.dao.candidate.CandidateRecruitmentPositionDao;
import ra.web.dto.candidate.CandidateRecruitmentPositionDto;
import ra.web.entity.RecruitmentPosition;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateRecruitmentPositionService {

    @Autowired
    private CandidateRecruitmentPositionDao positionDao;

    @Autowired
    private CandidatePositionDao applicationDao;

    public List<CandidateRecruitmentPositionDto> findActivePositions(int page, int size, String keyword, Integer candidateId) {
        return positionDao.findActivePositions(page, size, keyword).stream()
                .map(position -> {
                    boolean applied = applicationDao.existsByCandidateAndPosition(candidateId, position.getId());
                    return new CandidateRecruitmentPositionDto(position, applied);
                })
                .collect(Collectors.toList());
    }

    public long countActivePositions(String keyword) {
        return positionDao.countActivePositions(keyword);
    }

    public RecruitmentPosition findById(Integer id) {
        RecruitmentPosition position = positionDao.findById(id);
        if (position != null && position.getTechnologies() != null) {
            position.getTechnologies().size();
        }

        return position;
    }

    public boolean isApplied(Integer candidateId, Integer positionId) {
        return applicationDao.existsByCandidateAndPosition(candidateId, positionId);
    }
}