package ra.web.service.candidate;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.web.dao.candidate.IProfileDao;
import ra.web.dto.admin.CandidateTechnologyDto;
import ra.web.dto.candidate.ProfileDto;
import ra.web.entity.Candidate;
import ra.web.entity.Technology;

import java.util.Collections;
import java.util.Set;

@Service
public class ProfileService {

    @Autowired
    private IProfileDao profileDao;
    @Autowired
    private CandidateTechnologyService technologyService;

    /**
     * Lấy thông tin profile theo ID
     */
    public ProfileDto getProfileById(Integer candidateId) {
        Candidate candidate = profileDao.findById(candidateId);
        if (candidate != null) {
            return new ProfileDto(candidate);
        }
        return null;
    }

    /**
     * Lấy thông tin profile theo email
     */
    public ProfileDto getProfileByEmail(String email) {
        Candidate candidate = profileDao.findByEmail(email);
        if (candidate != null) {
            return new ProfileDto(candidate);
        }
        return null;
    }

    /**
     * Cập nhật thông tin profile
     */
    public boolean updateProfile(ProfileDto profileDto) {
        // Kiểm tra email đã tồn tại chưa (trừ candidate hiện tại)
        if (profileDao.existsByEmailAndNotId(profileDto.getEmail(), profileDto.getId())) {
            return false;
        }

        // Chuyển đổi DTO sang Entity và cập nhật
        Candidate candidate = profileDto.toEntity();
        return profileDao.updateProfile(candidate);
    }
    public Set<Technology> getCandidateTechnologies(Integer candidateId) {
        Candidate candidate = profileDao.findById(candidateId);
        return candidate != null ? candidate.getTechnologies() : Collections.emptySet();
    }

    public boolean updateCandidateTechnologies(CandidateTechnologyDto dto) {
        try {
            Candidate candidate = profileDao.findById(dto.getCandidateId());
            if (candidate == null) {
                return false;
            }

            Set<Technology> technologies = technologyService.findAllByIds(dto.getTechnologyIds());
            candidate.setTechnologies(technologies);
            profileDao.updateProfileWithTechnologies(candidate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(Integer candidateId, String currentPassword, String newPassword) {
        // Kiểm tra mật khẩu hiện tại
        if (!profileDao.checkCurrentPassword(candidateId, currentPassword)) {
            return false;
        }

        // Đổi mật khẩu
        return profileDao.changePassword(candidateId, BCrypt.hashpw(newPassword,BCrypt.gensalt()));
    }

    /**
     * Kiểm tra mật khẩu hiện tại
     */
    public boolean checkCurrentPassword(Integer candidateId, String currentPassword) {
        return profileDao.checkCurrentPassword(candidateId, currentPassword);
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    public boolean existsByEmailAndNotId(String email, Integer excludeId) {
        return profileDao.existsByEmailAndNotId(email, excludeId);
    }
}