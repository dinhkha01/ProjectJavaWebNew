package ra.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.web.dto.PageDto;
import ra.web.dto.admin.RecruitmentPositionDto;
import ra.web.entity.RecruitmentPosition;
import ra.web.entity.Technology;
import ra.web.service.admin.RecruitmentPositionService;
import ra.web.service.admin.TechnologyService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/positions")
public class RecruitmentPositionController {

    @Autowired
    private RecruitmentPositionService positionService;

    @Autowired
    private TechnologyService technologyService;

    @GetMapping
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            Model model
    ) {

        PageDto<RecruitmentPosition> pageDto = positionService.findAll(page, size, sortBy, direction, keyword);

        // Thống kê
        long totalPositions = positionService.countAll();
        long activePositions = positionService.countActive();
        long deletedPositions = positionService.countDeleted();

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("totalPositions", totalPositions);
        model.addAttribute("activePositions", activePositions);
        model.addAttribute("deletedPositions", deletedPositions);
        model.addAttribute("lastUpdate", positionService.getLastUpdate());
        System.out.println("===== DANH SÁCH CÔNG NGHỆ =====");
        for (RecruitmentPosition position : pageDto.getContent()) {
            System.out.println("Position: " + position.getName());
            if (position.getTechnologies() != null) {
                System.out.println("Technologies: " +
                        position.getTechnologies().stream()
                                .map(Technology::getName)
                                .collect(Collectors.joining(", ")));
            } else {
                System.out.println("No technologies");
            }
        }

        return "admin/recruitment-position/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("positionDto", new RecruitmentPositionDto());
        model.addAttribute("technologies", technologyService.findAllActiveTechnologies());
        return "admin/recruitment-position/add";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("positionDto") RecruitmentPositionDto dto,
            BindingResult result,
            Model model
    ) {
        // Thêm kiểm tra null cho expiredDate
        if (dto.getExpiredDate() == null) {
            result.rejectValue("expiredDate", "error.expiredDate", "Ngày hết hạn không được để trống");
        }
        // Kiểm tra ngày hết hạn
        if (dto.getExpiredDate() != null && dto.getExpiredDate().isBefore(LocalDate.now())) {
            result.rejectValue("expiredDate", "error.expiredDate", "Ngày hết hạn phải trong tương lai");
        }

        // Kiểm tra lương
        if (dto.getMaxSalary() != null && dto.getMinSalary() != null
                && dto.getMaxSalary().compareTo(dto.getMinSalary()) < 0) {
            result.rejectValue("maxSalary", "error.maxSalary", "Lương tối đa phải lớn hơn lương tối thiểu");
        }

        if (result.hasErrors()) {
            model.addAttribute("technologies", technologyService.findAllActiveTechnologies());
            return "admin/recruitment-position/add";
        }

        positionService.save(dto);
        return "redirect:/admin/positions";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        RecruitmentPositionDto dto = positionService.findById(id);
        if (dto == null) {
            return "redirect:/admin/positions";
        }

        model.addAttribute("positionDto", dto);
        model.addAttribute("technologies", technologyService.findAllActiveTechnologies());
        return "admin/recruitment-position/edit";
    }

    @PostMapping("/edit")
    public String edit(
            @Valid @ModelAttribute("positionDto") RecruitmentPositionDto dto,
            BindingResult result,
            Model model
    ) {
        // Thêm kiểm tra null cho expiredDate
        if (dto.getExpiredDate() == null) {
            result.rejectValue("expiredDate", "error.expiredDate", "Ngày hết hạn không được để trống");
        }
        // Kiểm tra ngày hết hạn
        if (dto.getExpiredDate() != null && dto.getExpiredDate().isBefore(LocalDate.now())) {
            result.rejectValue("expiredDate", "error.expiredDate", "Ngày hết hạn phải trong tương lai");
        }

        // Kiểm tra lương
        if (dto.getMaxSalary() != null && dto.getMinSalary() != null
                && dto.getMaxSalary().compareTo(dto.getMinSalary()) < 0) {
            result.rejectValue("maxSalary", "error.maxSalary", "Lương tối đa phải lớn hơn lương tối thiểu");
        }

        if (result.hasErrors()) {
            model.addAttribute("technologies", technologyService.findAllActiveTechnologies());
            return "admin/recruitment-position/edit";
        }

        positionService.save(dto);
        return "redirect:/admin/positions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        positionService.delete(id);
        return "redirect:/admin/positions";
    }
}