package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;

import java.util.List;

public interface IRegistrationService {
    List<RegistrationDTO> getAll();

    RegistrationDTO getById(Long id);

    List<RegistrationDTO> getByWorkshopId(Long workshopId);

    List<RegistrationDTO> getByUserId(Long userId);

    RegistrationDTO create(RegistrationDTO dto);

    RegistrationDTO update(Long id, RegistrationDTO dto);

    void delete(Long id);
}
