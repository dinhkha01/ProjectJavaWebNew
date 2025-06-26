package ra.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.web.service.PageService;

@Controller
public class PaginationController {
    @Autowired
    private PageService pageService;
    //  Phân trang là gì ?
    // phaan trang bao gôm các thành phần
    // 1. Số trang hiện tại,
    // 2. Tổng số trang,
    // 3. Các bản ghi trên trang hiện tại,

    // request phải gửi lên prameters nào : page, size , sort(by, direction), key word


    @GetMapping("/pagination")
    public  String pagination(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "5") int size,
            @RequestParam(value = "sort",defaultValue = "id") String sortBy,
            @RequestParam(value = "direction",defaultValue = "asc") String direction,
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            Model model
    ) {
//        model.addAttribute("pageDto",pageService.getUsersByPage(page, size, sortBy, direction, keyword));
        return "pagination"; // Tên của view sẽ là pagination.html
    }

}
