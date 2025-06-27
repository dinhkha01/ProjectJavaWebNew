package ra.web.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.web.dao.admin.RecruitmentPositionDao;
import ra.web.dao.admin.TechnologyDao;
import ra.web.dto.PageDto;
import ra.web.dto.admin.RecruitmentPositionDto;
import ra.web.entity.RecruitmentPosition;
import ra.web.entity.Technology;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecruitmentPositionService {

    @Autowired
    private RecruitmentPositionDao positionDao;

    @Autowired
    private TechnologyDao technologyDao;

    public PageDto<RecruitmentPosition> findAll(int page, int size, String sortBy, String direction, String keyword) {
        List<RecruitmentPosition> positions = positionDao.findAllByPage(page, size, sortBy, direction, keyword);
        long totalPages = (long) Math.ceil((double) positionDao.countAll(keyword) / size);

        return PageDto.<RecruitmentPosition>builder()
                .content(positions)
                .currentPage(page)
                .totalPages(totalPages)
                .size(size)
                .keyword(keyword)
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    public RecruitmentPositionDto findById(Integer id) {
        RecruitmentPosition position = positionDao.findById(id);
        if (position == null || position.getIsDeleted() || position.getName().endsWith("_deleted")) {
            return null;
        }
        return new RecruitmentPositionDto(position);
    }

    public RecruitmentPositionDto save(RecruitmentPositionDto dto) {
        RecruitmentPosition position = dto.toEntity();


        if (position.getId() == null) {
            position.setCreatedDate(LocalDate.now());
        }
        position.setLastUpdated(LocalDateTime.now());
        Set<Technology> technologies = dto.getTechnologyIds().stream()
                .map(techId -> technologyDao.findById(techId))
                .collect(Collectors.toSet());
        position.setTechnologies(technologies);

        RecruitmentPosition savedPosition = positionDao.save(position);
        return new RecruitmentPositionDto(savedPosition);
    }

    public void delete(Integer id) {
        positionDao.softDelete(id);
    }

    public long countAll() {
        return positionDao.countAllPositions();
    }

    public long countActive() {
        return positionDao.countActivePositions();
    }

    public long countDeleted() {
        return positionDao.countDeletedPositions();
    }

    public LocalDateTime getLastUpdate() {
        return positionDao.getLastUpdate();
    }
}