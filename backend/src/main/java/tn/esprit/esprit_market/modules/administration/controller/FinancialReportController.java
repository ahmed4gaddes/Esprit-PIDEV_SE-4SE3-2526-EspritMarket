package tn.esprit.esprit_market.modules.administration.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.service.IFinancialReportService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/financial-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinancialReportController {

    private final IFinancialReportService financialReportService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FinancialReportDTO> createFinancialReport(@RequestBody FinancialReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financialReportService.createFinancialReport(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FinancialReportDTO> updateFinancialReport(@PathVariable Long id, @RequestBody FinancialReportDTO dto) {
        return ResponseEntity.ok(financialReportService.updateFinancialReport(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<FinancialReportDTO> getFinancialReportById(@PathVariable Long id) {
        return ResponseEntity.ok(financialReportService.getFinancialReportById(id));
    }

    @GetMapping("/commission/{commissionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<List<FinancialReportDTO>> getReportsByCommission(@PathVariable Long commissionId) {
        return ResponseEntity.ok(financialReportService.getReportsByCommission(commissionId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<FinancialReportDTO>> getAllFinancialReports() {
        return ResponseEntity.ok(financialReportService.getAllFinancialReports());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFinancialReport(@PathVariable Long id) {
        financialReportService.deleteFinancialReport(id);
        return ResponseEntity.noContent().build();
    }
}
