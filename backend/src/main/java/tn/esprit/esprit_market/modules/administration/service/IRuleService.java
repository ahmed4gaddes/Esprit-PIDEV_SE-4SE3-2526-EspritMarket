package tn.esprit.esprit_market.modules.administration.service;

import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;

import java.util.List;

public interface IRuleService {
    RuleDTO createRule(Long adminId, RuleDTO dto);
    RuleDTO updateRule(Long id, RuleDTO dto);
    RuleDTO getRuleById(Long id);
    List<RuleDTO> getAllRules();
    List<RuleDTO> getRulesByCategory(RuleCategory category);
    List<RuleDTO> getMandatoryRules();
    void deleteRule(Long id);
}
