package ra.web.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.admin.TechnologyDao;
import ra.web.dto.PageDto;
import ra.web.dto.admin.TechnologyDto;
import ra.web.entity.Technology;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TechnologyService {

    @Autowired
    private TechnologyDao technologyDao;

    // Lấy danh sách công nghệ với phân trang
    public PageDto<TechnologyDto> getTechnologiesByPage(int page, int size, String sortBy, String direction, String keyword) {
        List<Technology> technologies = technologyDao.getTechnologiesByPage(page, size, sortBy, direction, keyword);
        List<TechnologyDto> technologyDtos = technologies.stream()
                .map(TechnologyDto::new)
                .collect(Collectors.toList());

        long totalPages = technologyDao.getTotalPages(size, keyword);

        return PageDto.<TechnologyDto>builder()
                .content(technologyDtos)
                .currentPage(page)
                .totalPages(totalPages)
                .size(size)
                .keyword(keyword)
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    // Lấy tất cả công nghệ (không phân trang)
    public List<TechnologyDto> getAllTechnologies() {
        return technologyDao.getAllTechnologies().stream()
                .map(TechnologyDto::new)
                .collect(Collectors.toList());
    }
    public List<Technology> findAllActiveTechnologies() {
        return technologyDao.getAllTechnologies().stream()
                .filter(tech -> !tech.getIsDeleted())
                .collect(Collectors.toList());
    }

    // Tìm theo ID
    public TechnologyDto findById(Integer id) {
        Technology technology = technologyDao.findById(id);
        return technology != null ? new TechnologyDto(technology) : null;
    }

    // Tìm theo tên
    public TechnologyDto findByName(String name) {
        Technology technology = technologyDao.findByName(name);
        return technology != null ? new TechnologyDto(technology) : null;
    }

    // Thêm mới công nghệ
    public TechnologyDto save(TechnologyDto technologyDto) throws Exception {
        // Validate tên không trùng
        if (technologyDao.existsByNameAndNotId(technologyDto.getName(), technologyDto.getId())) {
            throw new Exception("Tên công nghệ đã tồn tại!");
        }

        Technology technology = technologyDto.toEntity();
        Technology savedTechnology = technologyDao.save(technology);
        return new TechnologyDto(savedTechnology);
    }

    // Cập nhật công nghệ
    public TechnologyDto update(Integer id, TechnologyDto technologyDto) throws Exception {
        Technology existingTechnology = technologyDao.findById(id);
        if (existingTechnology == null) {
            throw new Exception("Không tìm thấy công nghệ với ID: " + id);
        }

        // Validate tên không trùng (loại trừ ID hiện tại)
        if (technologyDao.existsByNameAndNotId(technologyDto.getName(), id)) {
            throw new Exception("Tên công nghệ đã tồn tại!");
        }

        existingTechnology.setName(technologyDto.getName());
        Technology updatedTechnology = technologyDao.save(existingTechnology);
        return new TechnologyDto(updatedTechnology);
    }

    // Xóa công nghệ (soft delete)
    public void delete(Integer id) throws Exception {
        Technology technology = technologyDao.findById(id);
        if (technology == null) {
            throw new Exception("Không tìm thấy công nghệ với ID: " + id);
        }

        technologyDao.softDelete(id);
    }

    // Kiểm tra tên có tồn tại không
    public boolean existsByName(String name) {
        return technologyDao.existsByNameAndNotId(name, null);
    }

    // Kiểm tra tên có tồn tại không (loại trừ ID)
    public boolean existsByNameAndNotId(String name, Integer excludeId) {
        return technologyDao.existsByNameAndNotId(name, excludeId);
    }
    public long countAllTechnologies() {
        return technologyDao.countAll();
    }

    public long countActiveTechnologies() {
        return technologyDao.countActive();
    }

    public long countDeletedTechnologies() {
        return technologyDao.countDeleted();
    }
}
