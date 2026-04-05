package tn.esprit.esprit_market.modules.administration.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;
import tn.esprit.esprit_market.modules.administration.service.IRuleService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RuleController {

    private final IRuleService ruleService;
    private final IUserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RuleDTO> createRule(@Valid @RequestBody RuleDTO dto, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ruleService.createRule(currentUser.getId(), dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable Long id, @Valid @RequestBody RuleDTO dto) {
        return ResponseEntity.ok(ruleService.updateRule(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleDTO> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @GetMapping
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RuleDTO>> getRulesByCategory(@PathVariable RuleCategory category) {
        return ResponseEntity.ok(ruleService.getRulesByCategory(category));
    }

    @GetMapping("/mandatory")
    public ResponseEntity<List<RuleDTO>> getMandatoryRules() {
        return ResponseEntity.ok(ruleService.getMandatoryRules());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
