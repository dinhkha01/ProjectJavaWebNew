package ra.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.PageDto;
import ra.web.dto.admin.ApplicationDto;
import ra.web.entity.Application;
import ra.web.entity.Application.ProgressStatus;
import ra.web.service.admin.ApplicationService;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
@Controller
@RequestMapping("/admin/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public String listApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) ProgressStatus progress,
            @RequestParam(required = false) String result,
            Model model) {

        PageDto<ApplicationDto> pageDto = applicationService.findAllWithPagination(
                page, size, sortBy, direction, progress, result);

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("progressOptions", ProgressStatus.values());
        model.addAttribute("selectedProgress", progress);
        model.addAttribute("selectedResult", result);

        return "admin/application/list";
    }

    @GetMapping("/view/{id}")
    public String viewApplication(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Application application = applicationService.findById(id);
        if (application == null || application.getDestroyAt() != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn ứng tuyển không tồn tại hoặc đã bị huỷ");
            return "redirect:/admin/applications";
        }
        if (application.getProgress() == ProgressStatus.pending) {
                applicationService.viewApplication(id);
        }
        model.addAttribute("app", application);
        return "admin/application/view";
    }
    @PostMapping("/cancel/{id}")
    public String cancelApplication(
            @PathVariable Integer id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        boolean success = applicationService.cancelApplication(id, reason);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Huỷ đơn ứng tuyển thành công");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Huỷ đơn ứng tuyển thất bại");
        }
        return "redirect:/admin/applications";
    }

    @PostMapping("/interview/{id}")
    public String moveToInterviewing(
            @PathVariable Integer id,
            @RequestParam("interviewRequestDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewRequestDate,
            @RequestParam("interviewLink") String interviewLink,
            @RequestParam("interviewTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewTime,
            RedirectAttributes redirectAttributes) {
        boolean success = applicationService.moveToInterviewing(id, interviewRequestDate, interviewLink, interviewTime);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển sang phỏng vấn thành công");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Chuyển sang phỏng vấn thất bại");
        }
        return "redirect:/admin/applications/view/" + id;
    }

    @PostMapping("/result/{id}")
    public String updateInterviewResult(
            @PathVariable Integer id,
            @RequestParam String result,
            @RequestParam String note,
            RedirectAttributes redirectAttributes) {
        boolean success = applicationService.updateInterviewResult(id, result, note);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật kết quả phỏng vấn thành công");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật kết quả phỏng vấn thất bại");
        }
        return "redirect:/admin/applications/view/" + id;
    }
}