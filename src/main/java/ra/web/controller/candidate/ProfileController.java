package ra.web.controller.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.admin.CandidateTechnologyDto;
import ra.web.dto.admin.TechnologyDto;
import ra.web.dto.candidate.ChangePasswordDto;
import ra.web.dto.candidate.ProfileDto;
import ra.web.entity.Technology;
import ra.web.service.admin.TechnologyService;
import ra.web.service.candidate.ProfileService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/candidate")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // Kiểm tra đăng nhập
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
            return "redirect:/login";
        }

        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Lấy thông tin profile từ database
        ProfileDto profileDto = profileService.getProfileById(candidateId);
        if (profileDto == null) {
            return "redirect:/login";
        }

        // Lấy danh sách công nghệ
        Set<Technology> candidateTechnologies = profileService.getCandidateTechnologies(candidateId);
        Set<Integer> selectedTechIds = candidateTechnologies.stream()
                .map(Technology::getId)
                .collect(Collectors.toSet());

        // Lấy tất cả công nghệ ACTIVE
        List<Technology> allActiveTechnologies = technologyService.findAllActiveTechnologies();
        System.out.println("All active technologies: " + profileDto.getTechnologyNames());

        // Thêm vào model
        model.addAttribute("candidate", profileDto);
        model.addAttribute("candidateName", profileDto.getName());
        model.addAttribute("candidateEmail", profileDto.getEmail());
        model.addAttribute("allTechnologies", allActiveTechnologies);
        model.addAttribute("selectedTechIds", selectedTechIds);

        // Thêm DTO cho form cập nhật
        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", profileDto);
        }

        // Thêm DTO cho form đổi mật khẩu
        if (!model.containsAttribute("changePasswordDto")) {
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
        }

        return "candidate/profile";
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * Cập nhật thông tin profile
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
            return "redirect:/login";
        }

        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Set ID từ session
        profileDto.setId(candidateId);

        // Kiểm tra email đã tồn tại chưa
        if (profileService.existsByEmailAndNotId(profileDto.getEmail(), candidateId)) {
            bindingResult.rejectValue("email", "email.exists", "Email đã tồn tại trong hệ thống");
        }

        if (bindingResult.hasErrors()) {
            ProfileDto currentProfile = profileService.getProfileById(candidateId);
            model.addAttribute("candidate", currentProfile);
            model.addAttribute("candidateName", currentProfile.getName());
            model.addAttribute("candidateEmail", currentProfile.getEmail());
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
            return "candidate/profile";
        }
        if (bindingResult.hasFieldErrors("dob")) {
            // Lấy thông báo lỗi
            String errorMessage = "Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd";

            // Thêm thông báo lỗi vào model
            redirectAttributes.addFlashAttribute("updateError", errorMessage);
            return "redirect:/candidate/profile";
        }


        // Cập nhật thông tin
        boolean success = profileService.updateProfile(profileDto);

        if (success) {
            // Cập nhật session
            session.setAttribute("candidateName", profileDto.getName());
            session.setAttribute("candidateEmail", profileDto.getEmail());

            redirectAttributes.addFlashAttribute("updateSuccess", "Cập nhật thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("updateError", "Cập nhật thông tin thất bại!");
        }

        return "redirect:/candidate/profile";
    }
    @Autowired
    private TechnologyService technologyService;

    @GetMapping("/profile/technologies")
    public String getCandidateTechnologies(HttpSession session, Model model) {
        // Kiểm tra đăng nhập
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
            return "redirect:/login";
        }

        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Lấy danh sách công nghệ của candidate
        Set<Technology> candidateTechnologies = profileService.getCandidateTechnologies(candidateId);
        Set<Integer> selectedTechIds = candidateTechnologies.stream()
                .map(Technology::getId)
                .collect(Collectors.toSet());

        // Lấy tất cả công nghệ ACTIVE
        List<Technology> allActiveTechnologies = technologyService.findAllActiveTechnologies();

        model.addAttribute("allTechnologies", allActiveTechnologies);
        model.addAttribute("selectedTechIds", selectedTechIds);
        model.addAttribute("candidateId", candidateId);

        System.out.println("Danh sách công nghệ active: " + allActiveTechnologies);
        System.out.println("Công nghệ đã chọn: " + selectedTechIds);

        return "candidate/technology-fragment :: technologyContent";
    }

    @PostMapping("/profile/update-technologies")
    public String updateCandidateTechnologies(@ModelAttribute CandidateTechnologyDto dto,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {
        // Kiểm tra đăng nhập
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
            return "redirect:/login";
        }

        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        dto.setCandidateId(candidateId);
        boolean success = profileService.updateCandidateTechnologies(dto);

        if (success) {
            redirectAttributes.addFlashAttribute("updateSuccess", "Cập nhật công nghệ thành công!");
        } else {
            redirectAttributes.addFlashAttribute("updateError", "Cập nhật công nghệ thất bại!");
        }

        return "redirect:/candidate/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        // Kiểm tra đăng nhập
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null || !"CANDIDATE".equals(userLogin)) {
            return "redirect:/login";
        }

        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Kiểm tra mật khẩu mới và xác nhận
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra mật khẩu hiện tại
        if (!profileService.checkCurrentPassword(candidateId, changePasswordDto.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "password.incorrect", "Mật khẩu hiện tại không chính xác");
        }

        if (bindingResult.hasErrors()) {
            // Lấy lại thông tin candidate để hiển thị
            ProfileDto currentProfile = profileService.getProfileById(candidateId);
            model.addAttribute("candidate", currentProfile);
            model.addAttribute("candidateName", currentProfile.getName());
            model.addAttribute("candidateEmail", currentProfile.getEmail());
            model.addAttribute("profileDto", currentProfile);
            return "candidate/profile";
        }

        // Đổi mật khẩu
        boolean success = profileService.changePassword(candidateId,
                changePasswordDto.getCurrentPassword(),
                changePasswordDto.getNewPassword());

        if (success) {
            redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        } else {
            redirectAttributes.addFlashAttribute("passwordError", "Đổi mật khẩu thất bại!");
        }

        return "redirect:/candidate/profile";
    }
}