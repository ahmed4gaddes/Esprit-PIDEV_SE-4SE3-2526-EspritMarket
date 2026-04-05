import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SponsorshipRequestService } from '../../Services/sponsorship-request.service';
import { SponsorshipRequest } from '../../models/sponsorship-request';
import { InternshipListComponent } from '../internship-list/internship-list.component';

@Component({
    selector: 'app-company-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, InternshipListComponent],
    template: `
    <div class="dashboard-container container py-4">
      <div class="hero-card mb-4">
        <h1 class="mb-2">Company Sponsorship Offers</h1>
        <p class="text-muted mb-0">Create offers and monitor sponsor review progress.</p>
      </div>

      <div class="content-shell">
        <aside class="side-tabs">
          <button class="side-tab-btn" [class.active]="activeTab === 'create'" (click)="activeTab = 'create'">
            <i class="bi bi-plus-circle me-2"></i>Create Offer
            <i class="bi bi-chevron-right nav-arrow"></i>
          </button>
          <button class="side-tab-btn" [class.active]="activeTab === 'offers'" (click)="activeTab = 'offers'">
            <i class="bi bi-card-checklist me-2"></i>My Offers
            <i class="bi bi-chevron-right nav-arrow"></i>
          </button>
          <button class="side-tab-btn" [class.active]="activeTab === 'stages'" (click)="activeTab = 'stages'">
            <i class="bi bi-briefcase-fill me-2"></i>Offer de stage
            <i class="bi bi-chevron-right nav-arrow"></i>
          </button>
        </aside>

        <section class="content-panel">
          <div class="card shadow-sm modern-card mb-4" *ngIf="activeTab === 'create'">
            <div class="card-body">
              <h5 class="mb-3">Create Offer</h5>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Offer title</label>
                  <input class="form-control" [(ngModel)]="form.offerTitle" />
                </div>
                <div class="col-md-3">
                  <label class="form-label">Budget</label>
                  <input type="number" min="0" class="form-control" [(ngModel)]="form.budget" />
                </div>
                <div class="col-12">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" rows="3" [(ngModel)]="form.offerDescription"></textarea>
                </div>
                <div class="col-12">
                  <label class="form-label">Message for sponsor</label>
                  <textarea class="form-control" rows="2" [(ngModel)]="form.message"></textarea>
                </div>
                <div class="col-12">
                  <button class="btn btn-primary" (click)="submit()" [disabled]="submitting || !form.offerTitle">
                    {{ submitting ? 'Saving...' : 'Send Offer' }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="card shadow-sm modern-card" *ngIf="activeTab === 'offers'">
            <div class="card-body">
              <h5 class="mb-3">My Offers</h5>
              <div *ngIf="offers.length === 0" class="text-muted">No offers yet.</div>
              <div class="table-responsive" *ngIf="offers.length > 0">
                <table class="table table-sm align-middle">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Budget</th>
                      <th>Status</th>
                      <th>Sponsor</th>
                      <th>Design</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let o of offers">
                      <td>{{ o.offerTitle }}</td>
                      <td>{{ o.budget || 0 | currency }}</td>
                      <td><span class="badge" [ngClass]="getStateBadgeClass(o.state)">{{ o.state }}</span></td>
                      <td>{{ o.sponsorName || '-' }}</td>
                      <td>
                        <a *ngIf="o.sponsorDesignUrl" [href]="o.sponsorDesignUrl!" target="_blank" class="btn btn-sm btn-outline-primary">View design</a>
                        <span *ngIf="!o.sponsorDesignUrl">-</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div *ngIf="activeTab === 'stages'" class="mt-3">
            <app-internship-list></app-internship-list>
          </div>
        </section>
      </div>
    </div>
  `,
    styles: [`
    .dashboard-container {
      margin-top: 1rem;
    }
    .hero-card {
      background: linear-gradient(135deg, #f8f9ff, #eef2ff);
      border: 1px solid #e5e7eb;
      border-radius: 16px;
      padding: 1.2rem 1.4rem;
    }
    .modern-card {
      border: 1px solid #edf0f5;
      border-radius: 14px;
    }
    .content-shell {
      display: grid;
      grid-template-columns: 220px 1fr;
      gap: 1rem;
      align-items: start;
    }
    .side-tabs {
      padding: .5rem 0;
      display: flex;
      flex-direction: column;
      gap: .35rem;
      position: sticky;
      top: 1rem;
    }
    .side-tab-btn {
      border: none;
      background: transparent;
      color: #586b80;
      border-radius: 14px;
      padding: .8rem .95rem;
      font-weight: 600;
      text-align: left;
      display: flex;
      align-items: center;
      gap: .45rem;
      transition: all .2s ease;
    }
    .side-tab-btn:hover {
      background: #f5f7ff;
      color: #3f556b;
    }
    .side-tab-btn.active {
      color: #fff;
      background: linear-gradient(135deg, #c1272d, #9b59b6);
      box-shadow: 0 8px 20px rgba(193, 39, 45, 0.25);
    }
    .nav-arrow {
      margin-left: auto;
      opacity: 0;
      font-size: .8rem;
    }
    .side-tab-btn.active .nav-arrow {
      opacity: .85;
    }
    .content-panel {
      min-width: 0;
    }
    @media (max-width: 991px) {
      .content-shell {
        grid-template-columns: 1fr;
      }
      .side-tabs {
        position: static;
      }
    }
    .badge-approved {
      background: #198754;
      color: #fff;
    }
    .badge-rejected {
      background: #dc3545;
      color: #fff;
    }
    .badge-pending {
      background: #f59f00;
      color: #111;
    }
  `]
})
export class CompanyDashboardComponent implements OnInit {
    activeTab: 'create' | 'offers' | 'stages' = 'create';
    offers: SponsorshipRequest[] = [];
    submitting = false;
    form: SponsorshipRequest = { offerTitle: '', offerDescription: '', message: '', budget: 0 };

    constructor(private sponsorshipRequestService: SponsorshipRequestService) {}

    ngOnInit(): void {
      this.load();
    }

    load(): void {
      this.sponsorshipRequestService.getMyCompanyOffers().subscribe({
        next: (data) => this.offers = data || [],
        error: () => this.offers = []
      });
    }

    submit(): void {
      this.submitting = true;
      this.sponsorshipRequestService.createOffer(this.form).subscribe({
        next: () => {
          this.submitting = false;
          this.form = { offerTitle: '', offerDescription: '', message: '', budget: 0 };
          this.load();
        },
        error: (err) => {
          this.submitting = false;
          alert('Failed to create offer: ' + (err?.error?.message || err?.message || 'unknown error'));
        }
      });
    }

    getStateBadgeClass(state?: string): string {
      if (state === 'APPROVED') return 'badge-approved';
      if (state === 'REJECTED') return 'badge-rejected';
      return 'badge-pending';
    }
}
