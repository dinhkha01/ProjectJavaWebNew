package ra.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.web.dto.PageDto;
import ra.web.dto.admin.TechnologyDto;
import ra.web.service.admin.TechnologyService;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/technologies")
public class TechnologyController {

    @Autowired
    private TechnologyService technologyService;

    // Hiển thị danh sách công nghệ với phân trang
    @GetMapping
    public String listTechnologies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        PageDto<TechnologyDto> pageDto = technologyService.getTechnologiesByPage(page, size, sortBy, direction, keyword);
        model.addAttribute("pageDto", pageDto);

        // Thêm dữ liệu thống kê
        long totalTechnologies = technologyService.countAllTechnologies();
        long activeTechnologies = technologyService.countActiveTechnologies();
        long deletedTechnologies = technologyService.countDeletedTechnologies();

        model.addAttribute("totalTechnologies", totalTechnologies);
        model.addAttribute("activeTechnologies", activeTechnologies);
        model.addAttribute("deletedTechnologies", deletedTechnologies);
        model.addAttribute("lastUpdate", "Hôm nay"); // Có thể thay bằng dữ liệu thực tế

        return "admin/technology/list";
    }

    // Hiển thị form thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("technologyDto", new TechnologyDto());
        model.addAttribute("isEdit", false);
        return "admin/technology/add";
    }

    // Xử lý thêm mới
    @PostMapping("/add")
    public String addTechnology(@Valid @ModelAttribute TechnologyDto technologyDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/technology/add";
        }

        try {
            technologyService.save(technologyDto);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm công nghệ thành công!");
            return "redirect:/admin/technologies";
        } catch (Exception e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "admin/technology/add";
        }
    }

    // Hiển thị form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        TechnologyDto technologyDto = technologyService.findById(id);
        if (technologyDto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy công nghệ!");
            return "redirect:/admin/technologies";
        }

        model.addAttribute("technologyDto", technologyDto);

        return "admin/technology/edit";
    }

    // Xử lý cập nhật
    @PostMapping("/edit/{id}")
    public String updateTechnology(@PathVariable Integer id,
                                   @Valid @ModelAttribute TechnologyDto technologyDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (bindingResult.hasErrors()) {

            return "admin/technology/edit";
        }

        try {
            technologyService.update(id, technologyDto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật công nghệ thành công!");
            return "redirect:/admin/technologies";
        } catch (Exception e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());

            return "admin/technology/edit";
        }
    }

    // Xóa công nghệ
    @PostMapping("/delete/{id}")
    public String deleteTechnology(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            technologyService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa công nghệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/technologies";
    }

    // AJAX - Kiểm tra tên có tồn tại không
    @GetMapping("/check-name")
    @ResponseBody
    public boolean checkNameExists(@RequestParam String name, @RequestParam(required = false) Integer excludeId) {
        return technologyService.existsByNameAndNotId(name, excludeId);
    }
}