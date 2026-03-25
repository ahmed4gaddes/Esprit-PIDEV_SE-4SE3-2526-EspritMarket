package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;

import java.util.List;

public interface IServiceService {
    List<ServiceDTO> getAll();
    List<ServiceDTO> getMyServices(String email);

    ServiceDTO getById(Long id);

    ServiceDTO update(Long id, ServiceDTO dto, String userEmail);

    tn.esprit.esprit_market.modules.service.entity.Service getEntityById(Long id);

    void delete(Long id, String userEmail);
}
