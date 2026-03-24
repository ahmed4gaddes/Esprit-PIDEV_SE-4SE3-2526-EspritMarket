package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.InternshipDTO;

import java.util.List;

public interface IInternshipService {
    List<InternshipDTO> getAll();

    InternshipDTO getById(Long id);

    InternshipDTO create(InternshipDTO dto);

    InternshipDTO update(Long id, InternshipDTO dto);

    void delete(Long id);
}
