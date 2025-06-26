package ra.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class PageDto<T> {
    private List<T> content;
    private int currentPage;
    private long totalPages;
    private int size;
    private String keyword;
    private String sortBy;
    private String direction;

    // Thêm constructor public
    public PageDto() {}

    public PageDto(List<T> content, int currentPage, long totalPages, int size,
                   String keyword, String sortBy, String direction) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.size = size;
        this.keyword = keyword;
        this.sortBy = sortBy;
        this.direction = direction;
    }
}