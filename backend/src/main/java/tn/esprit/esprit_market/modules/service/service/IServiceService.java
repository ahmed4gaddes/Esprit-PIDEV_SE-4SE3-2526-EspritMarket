package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;

import java.util.List;

public interface IServiceService {
    List<ServiceDTO> getAll();

    ServiceDTO getById(Long id);

    ServiceDTO update(Long id, ServiceDTO dto);

    void delete(Long id);
}
