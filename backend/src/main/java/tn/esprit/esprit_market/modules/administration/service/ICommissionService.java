package tn.esprit.esprit_market.modules.administration.service;

import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;

import java.util.List;

public interface ICommissionService {
    CommissionDTO createCommission(CommissionDTO dto);
    CommissionDTO updateCommission(Long id, CommissionDTO dto);
    CommissionDTO getCommissionById(Long id);
    CommissionDTO getCommissionByStore(Long storeId);
    List<CommissionDTO> getAllCommissions();
    void deleteCommission(Long id);
}
