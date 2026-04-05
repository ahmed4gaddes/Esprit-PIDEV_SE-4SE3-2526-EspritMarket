package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;

import java.util.List;

public interface IWorkshopService {
    List<WorkshopDTO> getAll();

    WorkshopDTO getById(Long id);

    WorkshopDTO create(WorkshopDTO dto);

    WorkshopDTO update(Long id, WorkshopDTO dto);

    void delete(Long id);
}
