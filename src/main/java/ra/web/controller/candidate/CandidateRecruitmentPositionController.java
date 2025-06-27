package ra.web.controller.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.web.dto.PageDto;
import ra.web.dto.candidate.CandidateRecruitmentPositionDto;
import ra.web.entity.RecruitmentPosition;
import ra.web.service.candidate.CandidateRecruitmentPositionService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/candidate/positions")
public class CandidateRecruitmentPositionController {

    @Autowired
    private CandidateRecruitmentPositionService positionService;

    @GetMapping
    public String listPositions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model,
            HttpSession session) {

        Integer candidateId = (Integer) session.getAttribute("candidateId");

        PageDto<CandidateRecruitmentPositionDto> pageDto = new PageDto<>();
        pageDto.setContent(positionService.findActivePositions(page, size, keyword, candidateId));
        pageDto.setCurrentPage(page);
        pageDto.setTotalPages((int) Math.ceil((double) positionService.countActivePositions(keyword) / size));
        pageDto.setSize(size);
        pageDto.setKeyword(keyword);
        pageDto.setSortBy("id");
        pageDto.setDirection("desc");

        model.addAttribute("pageDto", pageDto);
        return "candidate/position-list";
    }

    @GetMapping("/{id}")
    public String viewPositionDetail(@PathVariable Integer id, Model model, HttpSession session) {
        Integer candidateId = (Integer) session.getAttribute("candidateId");
        RecruitmentPosition position = positionService.findById(id);

        if (position == null) {
            return "redirect:/candidate/positions";
        }

        boolean applied = positionService.isApplied(candidateId, id);

        model.addAttribute("position", position);
        model.addAttribute("applied", applied);
        return "candidate/position-detail";
    }
}