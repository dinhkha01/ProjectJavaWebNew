package ra.web.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.AuthenDao;
import ra.web.entity.Candidate;

@Service
@Transactional
public class AuthenService {
    @Autowired
    private AuthenDao authenDao;

    // Đăng ký candidate mới
    public boolean register(String name, String email, String password) {
        // Kiểm tra email đã tồn tại
        if (authenDao.existsByEmail(email)) {
            return false;
        }

        // Tạo candidate mới
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // Mã hóa password
        candidate.setStatus("active");
        candidate.setIsDeleted(false);
        candidate.setExperience(0);

        // Lưu vào database
        authenDao.save(candidate);
        return true;
    }

    public Candidate login(String email, String password) {
        Candidate candidate = authenDao.findByEmail(email);
        if (candidate != null) {
            // KIỂM TRA MẬT KHẨU BẰNG BCrypt
            if (BCrypt.checkpw(password, candidate.getPassword())) {
                // Kiểm tra trạng thái tài khoản
                if (!"active".equalsIgnoreCase(candidate.getStatus())) {
                    return null; // Tài khoản không active
                }
                return candidate;
            }
        }
        return null;
    }

    // Đăng nhập admin (hardcode)
    public boolean adminLogin(String email, String password) {
        // Tài khoản admin cứng
        return "admin@gmail.com".equals(email) && "admin123".equals(password);
    }

    // Tìm candidate theo email
    public Candidate findByEmail(String email) {
        return authenDao.findByEmail(email);
    }

    // Kiểm tra email tồn tại
    public boolean existsByEmail(String email) {
        return authenDao.existsByEmail(email);
    }
}