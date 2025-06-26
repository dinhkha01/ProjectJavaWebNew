package ra.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.PageDto;
import ra.web.dto.admin.CandidateDto;
import ra.web.service.admin.CandidateService;

@Controller
@RequestMapping("/admin/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping("")
    public String listCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "all") String gender,
            @RequestParam(defaultValue = "all") String technology,
            Model model) {

        PageDto<CandidateDto> pageDto = candidateService.findAllWithPagination(
                page, size, keyword, sortBy, direction,
                minExperience, maxExperience, minAge, maxAge, gender, technology);

        // Thống kê
        long totalCandidates = candidateService.getTotalCandidates();
        long activeCandidates = candidateService.getActiveCandidates();
        long inactiveCandidates = candidateService.getInactiveCandidates();
        long deletedCandidates = candidateService.getDeletedCandidates();

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("totalCandidates", totalCandidates);
        model.addAttribute("activeCandidates", activeCandidates);
        model.addAttribute("inactiveCandidates", inactiveCandidates);
        model.addAttribute("deletedCandidates", deletedCandidates);

        // Thêm filter parameters để giữ lại trạng thái form
        model.addAttribute("minExperience", minExperience);
        model.addAttribute("maxExperience", maxExperience);
        model.addAttribute("minAge", minAge);
        model.addAttribute("maxAge", maxAge);
        model.addAttribute("selectedGender", gender);
        model.addAttribute("selectedTechnology", technology);

        return "admin/candidates/list";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = candidateService.toggleStatus(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái ứng viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật trạng thái ứng viên!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/candidates";
    }

    @GetMapping("/view/{id}")
    public String viewCandidate(@PathVariable Integer id, Model model) {

        return "admin/candidates/view";
    }
}