package ra.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.web.dao.PaginationDao;
import ra.web.dto.PageDto;

@Service
public class PageService {
//    @Autowired
//    private PaginationDao paginationDao;
//    public PageDto getUsersByPage(int page, int size, String sortBy, String direction, String keyword) {
//        return PageDto.builder()
//                .content(paginationDao.getUsersByPage(page, size, sortBy, direction, keyword))
//                .currentPage(page)
//                .totalPages(paginationDao.totalPages(size, keyword))
//                .size(size)
//                .keyword(keyword)
//                .sortBy(sortBy)
//                .direction(direction)
//                .build();
//    }

}
