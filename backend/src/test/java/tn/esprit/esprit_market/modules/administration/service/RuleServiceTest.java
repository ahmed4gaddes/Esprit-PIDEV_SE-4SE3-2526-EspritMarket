package tn.esprit.esprit_market.modules.administration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.RuleRepository;
import tn.esprit.esprit_market.modules.administration.service.impl.RuleServiceImpl;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private AdministrationMapper administrationMapper;

    @InjectMocks
    private RuleServiceImpl ruleService;

    private Rule rule;
    private RuleDTO ruleDTO;
    private User admin;

    @BeforeEach
    void setUp() {
        admin = User.builder().id(1L).email("admin@test.com").build();
        rule = Rule.builder().id(1L).title("Test Rule").category(RuleCategory.LEGAL_COMPLIANCE).active(true).build();
        ruleDTO = RuleDTO.builder().id(1L).title("Test Rule").category(RuleCategory.LEGAL_COMPLIANCE).active(true).build();
    }

    @Test
    void createRule_ShouldReturnSavedRule() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(administrationMapper.toRuleEntity(any(), any(), any())).thenReturn(rule);
        when(ruleRepository.save(any())).thenReturn(rule);
        when(administrationMapper.toRuleDTO(any())).thenReturn(ruleDTO);

        RuleDTO result = ruleService.createRule(1L, ruleDTO);

        assertNotNull(result);
        assertEquals("Test Rule", result.getTitle());
        verify(ruleRepository, times(1)).save(any());
    }

    @Test
    void createRule_AdminNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ruleService.createRule(1L, ruleDTO));
        verify(ruleRepository, never()).save(any());
    }

    @Test
    void getRuleById_ShouldReturnRule() {
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(administrationMapper.toRuleDTO(rule)).thenReturn(ruleDTO);

        RuleDTO result = ruleService.getRuleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllRules_ShouldReturnList() {
        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(administrationMapper.toRuleDTO(any())).thenReturn(ruleDTO);

        List<RuleDTO> rules = ruleService.getAllRules();

        assertFalse(rules.isEmpty());
        assertEquals(1, rules.size());
    }

    @Test
    void deleteRule_ShouldDeleteWhenExists() {
        when(ruleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ruleRepository).deleteById(1L);

        assertDoesNotThrow(() -> ruleService.deleteRule(1L));
        verify(ruleRepository, times(1)).deleteById(1L);
    }
}
