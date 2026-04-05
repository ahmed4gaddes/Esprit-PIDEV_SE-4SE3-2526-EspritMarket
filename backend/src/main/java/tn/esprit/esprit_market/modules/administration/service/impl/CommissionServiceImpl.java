package tn.esprit.esprit_market.modules.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.CommissionRepository;
import tn.esprit.esprit_market.modules.administration.service.ICommissionService;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements ICommissionService {

    private final CommissionRepository commissionRepository;
    private final StoreRepository storeRepository;
    private final AdministrationMapper administrationMapper;

    @Override
    @Transactional
    public CommissionDTO createCommission(CommissionDTO dto) {
        Store store = null;
        if (dto.getStoreId() != null) {
            store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found: " + dto.getStoreId()));
        }

        Commission commission = administrationMapper.toCommissionEntity(dto, store);
        Commission saved = commissionRepository.save(commission);
        return administrationMapper.toCommissionDTO(saved);
    }

    @Override
    @Transactional
    public CommissionDTO updateCommission(Long id, CommissionDTO dto) {
        Commission existing = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + id));

        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found: " + dto.getStoreId()));
            existing.setStore(store);
        }

        existing.setRate(dto.getRate());
        existing.setAmount(dto.getAmount());

        Commission saved = commissionRepository.save(existing);
        return administrationMapper.toCommissionDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionDTO getCommissionById(Long id) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + id));
        return administrationMapper.toCommissionDTO(commission);
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionDTO getCommissionByStore(Long storeId) {
        Commission commission = commissionRepository.findByStore_Id(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found for store: " + storeId));
        return administrationMapper.toCommissionDTO(commission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionDTO> getAllCommissions() {
        return commissionRepository.findAll().stream()
                .map(administrationMapper::toCommissionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommission(Long id) {
        if (!commissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commission not found: " + id);
        }
        commissionRepository.deleteById(id);
    }
}
