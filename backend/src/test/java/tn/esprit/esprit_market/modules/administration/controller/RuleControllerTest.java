package tn.esprit.esprit_market.modules.administration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;
import tn.esprit.esprit_market.modules.administration.service.IRuleService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    @Mock private IRuleService ruleService;
    @Mock private IUserService userService;
    @Mock private Authentication authentication;
    @InjectMocks private RuleController controller;
    private RuleDTO dto;
    private User adminUser;

    @BeforeEach
    void setUp() {
        dto = RuleDTO.builder().id(1L).build();
        adminUser = new User(); adminUser.setId(1L); adminUser.setEmail("admin@gmail.com");
    }

    @Test void testCreate() {
        when(authentication.getName()).thenReturn("admin@gmail.com");
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(adminUser);
        when(ruleService.createRule(eq(1L), any(RuleDTO.class))).thenReturn(dto);
        ResponseEntity<RuleDTO> res = controller.createRule(dto, authentication);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(ruleService.updateRule(eq(1L), any(RuleDTO.class))).thenReturn(dto);
        ResponseEntity<RuleDTO> res = controller.updateRule(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(ruleService.getRuleById(1L)).thenReturn(dto);
        ResponseEntity<RuleDTO> res = controller.getRuleById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetAll() {
        when(ruleService.getAllRules()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RuleDTO>> res = controller.getAllRules();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetByCategory() {
        when(ruleService.getRulesByCategory(RuleCategory.LEGAL_COMPLIANCE)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RuleDTO>> res = controller.getRulesByCategory(RuleCategory.LEGAL_COMPLIANCE);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetMandatory() {
        when(ruleService.getMandatoryRules()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RuleDTO>> res = controller.getMandatoryRules();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(ruleService).deleteRule(1L);
        ResponseEntity<Void> res = controller.deleteRule(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
