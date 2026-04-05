package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.CourseDTO;

import java.util.List;

public interface ICourseService {
    List<CourseDTO> getAll();

    CourseDTO getById(Long id);

    CourseDTO create(CourseDTO dto);

    CourseDTO update(Long id, CourseDTO dto);

    void delete(Long id);
}
