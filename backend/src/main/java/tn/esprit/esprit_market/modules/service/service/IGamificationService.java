package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.GamificationDTO;

import java.util.List;

public interface IGamificationService {
    List<GamificationDTO> getAll();

    GamificationDTO getById(Long id);

    GamificationDTO getByUserId(Long userId);

    GamificationDTO create(GamificationDTO dto);

    GamificationDTO update(Long id, GamificationDTO dto);

    void delete(Long id);
}
