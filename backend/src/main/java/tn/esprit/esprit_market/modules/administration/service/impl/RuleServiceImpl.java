package tn.esprit.esprit_market.modules.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.RuleRepository;
import tn.esprit.esprit_market.modules.administration.service.IRuleService;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements IRuleService {

    private final RuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final AdministrationMapper administrationMapper;

    private User getAdmin(Long adminId) {
        if (adminId == null) return null;
        return userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
    }

    private Set<Store> getStores(Set<Long> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(storeRepository.findAllById(storeIds));
    }

    @Override
    @Transactional
    public RuleDTO createRule(Long adminId, RuleDTO dto) {
        User admin = getAdmin(adminId);
        Set<Store> stores = getStores(dto.getAppliesToStoreIds());

        Rule rule = administrationMapper.toRuleEntity(dto, admin, stores);
        Rule saved = ruleRepository.save(rule);
        return administrationMapper.toRuleDTO(saved);
    }

    @Override
    @Transactional
    public RuleDTO updateRule(Long id, RuleDTO dto) {
        Rule existing = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));

        existing.setTitle(dto.getTitle());
        existing.setText(dto.getText());
        existing.setCategory(dto.getCategory());
        existing.setActive(dto.isActive());
        existing.setMandatory(dto.isMandatory());

        Set<Store> stores = getStores(dto.getAppliesToStoreIds());
        existing.setAppliesTo(stores);

        Rule saved = ruleRepository.save(existing);
        return administrationMapper.toRuleDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDTO getRuleById(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));
        return administrationMapper.toRuleDTO(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleDTO> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(administrationMapper::toRuleDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleDTO> getRulesByCategory(RuleCategory category) {
        return ruleRepository.findByCategory(category).stream()
                .map(administrationMapper::toRuleDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleDTO> getMandatoryRules() {
        return ruleRepository.findByMandatoryTrue().stream()
                .map(administrationMapper::toRuleDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        if (!ruleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rule not found: " + id);
        }
        ruleRepository.deleteById(id);
    }
}
