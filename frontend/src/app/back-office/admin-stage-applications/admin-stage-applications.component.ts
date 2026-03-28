import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, InternshipApplicationAdmin } from '../../core/services/admin.service';

@Component({
  selector: 'app-admin-stage-applications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-stage-applications.component.html',
  styleUrls: ['./admin-stage-applications.component.css']
})
export class AdminStageApplicationsComponent implements OnInit {
  loading = false;
  errorMessage = '';
  applications: InternshipApplicationAdmin[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  searchTerm = '';
  selectedStatus = 'ALL';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications() {
    this.loading = true;
    this.errorMessage = '';
    this.adminService.getInternshipApplications({
      status: this.selectedStatus,
      q: this.searchTerm,
      page: this.currentPage,
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.applications = data.items;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load internship applications.';
        this.loading = false;
      }
    });
  }

  formatDate(value?: string) {
    if (!value) {
      return '-';
    }
    return new Date(value).toLocaleString();
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadApplications();
  }

  previousPage() {
    if (this.currentPage === 0) {
      return;
    }
    this.currentPage--;
    this.loadApplications();
  }

  nextPage() {
    if (this.currentPage + 1 >= this.totalPages) {
      return;
    }
    this.currentPage++;
    this.loadApplications();
  }
}
