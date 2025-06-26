package ra.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.CandidateRegistrationDto;
import ra.web.dto.LoginDto;
import ra.web.entity.Candidate;
import ra.web.service.AuthenService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthController {
    @Autowired
    private AuthenService authenService;

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("candidateDto", new CandidateRegistrationDto());
        return "authen/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("candidateDto") CandidateRegistrationDto candidateDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu xác nhận
        if (!candidateDto.getPassword().equals(candidateDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra email đã tồn tại
        if (authenService.existsByEmail(candidateDto.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "Email đã tồn tại trong hệ thống");
        }

        if (bindingResult.hasErrors()) {
            return "authen/register";
        }

        // Thực hiện đăng ký
        boolean success = authenService.register(candidateDto.getName(), candidateDto.getEmail(), candidateDto.getPassword());

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } else {
            bindingResult.rejectValue("email", "registration.failed", "Đăng ký thất bại");
            return "authen/register";
        }
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "authen/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                        BindingResult bindingResult,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "authen/login";
        }

        if ("admin".equals(loginDto.getUserType())) {
            // Đăng nhập admin
            if (authenService.adminLogin(loginDto.getEmail(), loginDto.getPassword())) {
                session.setAttribute("userLogin", "ADMIN");
                session.setAttribute("adminEmail", loginDto.getEmail());
                return "redirect:/admin/dashboard";
            } else {
                bindingResult.rejectValue("password", "login.failed", "Thông tin đăng nhập admin không chính xác");
                return "authen/login";
            }
        } else {
            // Đăng nhập candidate
            Candidate candidate = authenService.login(loginDto.getEmail(), loginDto.getPassword());
            if (candidate != null) {
                // Lưu thông tin candidate vào session
                session.setAttribute("userLogin", "CANDIDATE");
                session.setAttribute("candidateId", candidate.getId());
                session.setAttribute("candidateName", candidate.getName());
                session.setAttribute("candidateEmail", candidate.getEmail());
                return "redirect:/candidate/profile";
            } else {
                // Kiểm tra xem có phải do tài khoản không active không
                Candidate inactiveCandidate = authenService.findByEmail(loginDto.getEmail());
                if (inactiveCandidate != null && !"active".equalsIgnoreCase(inactiveCandidate.getStatus())) {
                    bindingResult.rejectValue("password", "login.failed", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                } else {
                    bindingResult.rejectValue("password", "login.failed", "Email hoặc mật khẩu không chính xác");
                }
                return "authen/login";
            }
        }
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Xóa toàn bộ session
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công");
        return "redirect:/login";
    }

    // Trang profile candidate (yêu cầu đăng nhập)
//    @GetMapping("/candidate/profile")
//    public String candidateProfile(HttpSession session, Model model) {
//        Object userLogin = session.getAttribute("userLogin");
//        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
//            return "redirect:/login";
//        }
//
//        String candidateName = (String) session.getAttribute("candidateName");
//        String candidateEmail = (String) session.getAttribute("candidateEmail");
//
//        model.addAttribute("candidateName", candidateName);
//        model.addAttribute("candidateEmail", candidateEmail);
//
//        return "candidate/profile";
//    }

    // Dashboard admin (yêu cầu đăng nhập admin)
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"ADMIN".equals(userLogin)) {
            return "redirect:/login";
        }

        String adminEmail = (String) session.getAttribute("adminEmail");
        model.addAttribute("adminEmail", adminEmail);

        return "admin/dashboard";
    }

    // Trang chủ
    @GetMapping("/")
    public String home() {
        return "redirect:/candidate/profile";
    }
}