import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminAuditLog, AdminService } from '../../core/services/admin.service';

@Component({
  selector: 'app-admin-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-audit-logs.component.html',
  styleUrls: ['./admin-audit-logs.component.css']
})
export class AdminAuditLogsComponent implements OnInit {
  logs: AdminAuditLog[] = [];
  actionFilter = '';
  searchTerm = '';
  loading = false;
  errorMessage = '';
  page = 0;
  size = 20;
  totalPages = 0;
  totalElements = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.loading = true;
    this.errorMessage = '';
    this.adminService.getAuditLogs({
      action: this.actionFilter,
      q: this.searchTerm,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.logs = resp.items || [];
        this.page = resp.page ?? 0;
        this.totalPages = resp.totalPages ?? 0;
        this.totalElements = resp.totalElements ?? 0;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load audit logs.';
      }
    });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadLogs();
  }

  previousPage(): void {
    if (this.page <= 0) return;
    this.page--;
    this.loadLogs();
  }

  nextPage(): void {
    if (this.page + 1 >= this.totalPages) return;
    this.page++;
    this.loadLogs();
  }
}
