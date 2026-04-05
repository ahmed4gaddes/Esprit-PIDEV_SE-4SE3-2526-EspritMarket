package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.ServiceCalendarDTO;

import java.util.List;

public interface IServiceCalendarService {
    List<ServiceCalendarDTO> getAll();

    ServiceCalendarDTO getById(Long id);

    ServiceCalendarDTO create(ServiceCalendarDTO dto);

    ServiceCalendarDTO update(Long id, ServiceCalendarDTO dto);

    void delete(Long id);
}
