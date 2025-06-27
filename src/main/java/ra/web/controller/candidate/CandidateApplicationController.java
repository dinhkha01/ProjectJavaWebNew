// CandidateApplicationController.java
package ra.web.controller.candidate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.web.dto.PageDto;
import ra.web.dto.candidate.CandidateApplicationDto;
import ra.web.service.candidate.CandidateApplicationService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/candidate/applications")
@RequiredArgsConstructor
public class CandidateApplicationController {
    private final CandidateApplicationService applicationService;
    private static final int DEFAULT_PAGE_SIZE = 5;

    @GetMapping
    public String viewApplications(
            @RequestParam(defaultValue = "1") int page,
            HttpSession session,
            Model model) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        PageDto<CandidateApplicationDto> applications = applicationService.getApplications(candidateId, page, DEFAULT_PAGE_SIZE);
        model.addAttribute("applications", applications);
        return "candidate/applications/list";
    }

    @GetMapping("/{id}")
    public String viewApplicationDetails(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        CandidateApplicationDto application = applicationService.getApplicationDetails(id, candidateId);
        model.addAttribute("app", application);

        return "candidate/applications/detail";
    }

    @PostMapping("/{id}/confirm-interview")
    public String handleInterviewConfirmation(
            @PathVariable Integer id,
            @RequestParam boolean isConfirmed,
            @RequestParam(required = false) String response,
            HttpSession session) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        if (candidateId == null) {
            return "redirect:/login";
        }

        applicationService.confirmInterview(id, candidateId, isConfirmed, response);
        return "redirect:/candidate/applications/" + id;
    }
}