package ra.web.controller.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.candidate.ApplicationRequest;
import ra.web.service.candidate.CandidateApplyPositionService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/candidate/positions/apply")
public class CandidatePositionController {

    @Autowired
    private CandidateApplyPositionService applicationService;

    @GetMapping("/{positionId}")
    public String showApplyForm(@PathVariable("positionId") Integer positionId,
                                Model model,
                                HttpSession session) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Kiểm tra positionId hợp lệ
        if (positionId == null || positionId <= 0) {
            return "redirect:/candidate/positions";
        }

        // Kiểm tra xem đã apply chưa
        if (applicationService.isApplied(candidateId, positionId)) {
            return "redirect:/candidate/positions/" + positionId;
        }

        // Tạo request object và set positionId
        ApplicationRequest request = new ApplicationRequest();
        request.setPositionId(positionId);

        model.addAttribute("applicationRequest", request);
        return "candidate/apply-form";
    }

    @PostMapping
    public String submitApplication(@ModelAttribute("applicationRequest") ApplicationRequest request,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        // Kiểm tra positionId hợp lệ
        if (request.getPositionId() == null || request.getPositionId() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vị trí tuyển dụng không hợp lệ");
            return "redirect:/candidate/positions";
        }

        try {
            applicationService.apply(candidateId, request.getPositionId(), request.getCvUrl());
            redirectAttributes.addFlashAttribute("successMessage", "Nộp đơn thành công!");
            return "redirect:/candidate/positions/" + request.getPositionId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nộp đơn thất bại: " + e.getMessage());
            return "redirect:/candidate/positions/apply/" + request.getPositionId();
        }
    }
}