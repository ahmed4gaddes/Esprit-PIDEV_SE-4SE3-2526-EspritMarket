package tn.esprit.esprit_market.modules.administration.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.service.ICommissionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/commissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommissionController {

    private final ICommissionService commissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CommissionDTO> createCommission(@RequestBody CommissionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commissionService.createCommission(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CommissionDTO> updateCommission(@PathVariable Long id, @RequestBody CommissionDTO dto) {
        return ResponseEntity.ok(commissionService.updateCommission(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<CommissionDTO> getCommissionById(@PathVariable Long id) {
        return ResponseEntity.ok(commissionService.getCommissionById(id));
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<CommissionDTO> getCommissionByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(commissionService.getCommissionByStore(storeId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CommissionDTO>> getAllCommissions() {
        return ResponseEntity.ok(commissionService.getAllCommissions());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCommission(@PathVariable Long id) {
        commissionService.deleteCommission(id);
        return ResponseEntity.noContent().build();
    }
}
