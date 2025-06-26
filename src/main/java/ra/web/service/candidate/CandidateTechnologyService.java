package ra.web.service.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.web.dao.admin.TechnologyDao;
import ra.web.entity.Technology;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CandidateTechnologyService {

    @Autowired
    private TechnologyDao technologyDao;

    public List<Technology> findAllActiveTechnologies() {
        return technologyDao.getAllTechnologies().stream()
                .filter(tech -> !tech.getIsDeleted())
                .collect(Collectors.toList());
    }

    public Set<Technology> findAllByIds(Set<Integer> ids) {
        return ids != null ?
                ids.stream()
                        .map(id -> technologyDao.findById(id))
                        .filter(tech -> tech != null && !tech.getIsDeleted())
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
    }
}