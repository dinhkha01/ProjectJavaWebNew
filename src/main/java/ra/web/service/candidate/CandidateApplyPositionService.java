package ra.web.service.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.candidate.CandidatePositionDao;
import ra.web.entity.Application;
import ra.web.entity.Candidate;
import ra.web.entity.RecruitmentPosition;

@Service
public class CandidateApplyPositionService {

    @Autowired
    private CandidatePositionDao applicationDao;

    @Transactional
    public void apply(Integer candidateId, Integer positionId, String cvUrl) {
        // Kiểm tra xem đã apply chưa
        if (applicationDao.existsByCandidateAndPosition(candidateId, positionId)) {
            throw new RuntimeException("Bạn đã nộp đơn cho vị trí này");
        }

        // Tạo mới Application
        Application application = new Application();
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        application.setCandidate(candidate);

        RecruitmentPosition position = new RecruitmentPosition();
        position.setId(positionId);
        application.setRecruitmentPosition(position);

        application.setCvUrl(cvUrl);
        applicationDao.save(application);
    }

    public boolean isApplied(Integer candidateId, Integer positionId) {
        return applicationDao.existsByCandidateAndPosition(candidateId, positionId);
    }
}