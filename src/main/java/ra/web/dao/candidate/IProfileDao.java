package ra.web.dao.candidate;

import ra.web.entity.Candidate;

public interface IProfileDao {

    /**
     * Tìm candidate theo ID
     */
    Candidate findById(Integer id);

    /**
     * Tìm candidate theo email
     */
    Candidate findByEmail(String email);

    /**
     * Cập nhật thông tin candidate
     */
    boolean updateProfile(Candidate candidate);

    /**
     * Đổi mật khẩu
     */
    boolean changePassword(Integer candidateId, String newPassword);

    /**
     * Kiểm tra mật khẩu hiện tại
     */
    boolean checkCurrentPassword(Integer candidateId, String currentPassword);

    /**
     * Kiểm tra email đã tồn tại (trừ candidate hiện tại)
     */
    boolean existsByEmailAndNotId(String email, Integer excludeId);
    /**
     * Cập nhật thông tin candidate (bao gồm cả technologies)
     */
    boolean updateProfileWithTechnologies(Candidate candidate);
}