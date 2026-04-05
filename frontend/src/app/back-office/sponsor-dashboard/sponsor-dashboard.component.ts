import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SponsorshipRequestService } from '../../Services/sponsorship-request.service';
import { SponsorshipRequest } from '../../models/sponsorship-request';
import { UploadService } from '../../core/services/upload.service';

@Component({
    selector: 'app-sponsor-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="dashboard-container container py-4">
      <div class="hero-card mb-4">
        <h1 class="mb-2">Sponsor Offer Review</h1>
        <p class="text-muted mb-0">Review company offers, approve/reject requests, and upload the final design.</p>
      </div>

      <div class="content-shell">
        <aside class="side-tabs">
          <button class="side-tab-btn" [class.active]="activeTab === 'pending'" (click)="activeTab = 'pending'">
            <i class="bi bi-inbox me-2"></i>Pending Offers
            <i class="bi bi-chevron-right nav-arrow"></i>
          </button>
          <button class="side-tab-btn" [class.active]="activeTab === 'history'" (click)="activeTab = 'history'">
            <i class="bi bi-clock-history me-2"></i>Decisions History
            <i class="bi bi-chevron-right nav-arrow"></i>
          </button>
        </aside>

        <section class="content-panel">
          <div class="card shadow-sm modern-card mb-4" *ngIf="activeTab === 'pending'">
            <div class="card-body">
              <h5 class="mb-3">Pending Offers</h5>
              <div *ngIf="inbox.length === 0" class="text-muted">No pending offers.</div>
              <div *ngFor="let r of inbox" class="offer-item p-3 mb-3">
                <div class="d-flex justify-content-between align-items-start gap-3">
                  <div>
                    <div class="fw-bold fs-6">{{ r.offerTitle }}</div>
                    <div class="small text-muted">Company: {{ r.companyName || ('#' + r.companyId) }}</div>
                  </div>
                  <span class="badge" [ngClass]="getStateBadgeClass(r.state)">{{ r.state }}</span>
                </div>

                <p class="mb-1 mt-2">{{ r.offerDescription || '-' }}</p>
                <p class="small text-muted mb-2">Budget: {{ (r.budget || 0) | currency }}</p>

                <div class="mb-2">
                  <label class="form-label small">Design image (required for approval)</label>
                  <input type="file" class="form-control form-control-sm" accept="image/*" (change)="onDesignSelected(r.id!, $event)" />
                  <div class="small text-primary mt-1" *ngIf="decisionMap[r.id!]?.uploading">Uploading design...</div>
                  <div class="small text-success mt-1" *ngIf="decisionMap[r.id!]?.designUrl">Design uploaded.</div>
                </div>

                <div class="mb-2">
                  <label class="form-label small">Note</label>
                  <textarea class="form-control form-control-sm" rows="2" [(ngModel)]="decisionMap[r.id!].note"></textarea>
                </div>

                <div class="d-flex gap-2">
                  <button class="btn btn-sm btn-success" [disabled]="decisionMap[r.id!].uploading" (click)="decide(r.id!, true)">Approve</button>
                  <button class="btn btn-sm btn-outline-danger" [disabled]="decisionMap[r.id!].uploading" (click)="decide(r.id!, false)">Reject</button>
                </div>
              </div>
            </div>
          </div>

          <div class="card shadow-sm modern-card" *ngIf="activeTab === 'history'">
            <div class="card-body">
              <h5 class="mb-3">My Decisions History</h5>
              <div *ngIf="history.length === 0" class="text-muted">No decisions yet.</div>
              <ul class="list-group list-group-flush" *ngIf="history.length > 0">
                <li class="list-group-item px-0" *ngFor="let h of history">
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <div class="fw-semibold">{{ h.offerTitle }}</div>
                      <div class="small text-muted">{{ h.companyName || ('#' + h.companyId) }}</div>
                    </div>
                    <span class="badge" [ngClass]="getStateBadgeClass(h.state)">{{ h.state }}</span>
                  </div>
                </li>
              </ul>
            </div>
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
    .offer-item {
      border: 1px solid #edf0f5;
      border-radius: 12px;
      background: #fff;
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
export class SponsorDashboardComponent implements OnInit {
    activeTab: 'pending' | 'history' = 'pending';
    inbox: SponsorshipRequest[] = [];
    history: SponsorshipRequest[] = [];
    decisionMap: Record<number, { designUrl?: string; note?: string; uploading?: boolean }> = {};

    constructor(
      private sponsorshipRequestService: SponsorshipRequestService,
      private uploadService: UploadService
    ) {}

    ngOnInit(): void {
      this.reload();
    }

    reload(): void {
      this.sponsorshipRequestService.getSponsorInbox().subscribe({
        next: (data) => {
          this.inbox = data || [];
          for (const r of this.inbox) {
            if (r.id && !this.decisionMap[r.id]) {
              this.decisionMap[r.id] = { designUrl: '', note: '', uploading: false };
            }
          }
        },
        error: () => this.inbox = []
      });

      this.sponsorshipRequestService.getSponsorHistory().subscribe({
        next: (data) => this.history = data || [],
        error: () => this.history = []
      });
    }

    decide(id: number, approved: boolean): void {
      const payload = this.decisionMap[id] || {};
      if (approved && !payload.designUrl) {
        alert('Please upload a design image before approving.');
        return;
      }
      this.sponsorshipRequestService.decide(id, {
        approved,
        designUrl: payload.designUrl,
        note: payload.note
      }).subscribe({
        next: () => this.reload(),
        error: (err) => alert('Decision failed: ' + (err?.error?.message || err?.message || 'unknown error'))
      });
    }

    onDesignSelected(requestId: number, event: any): void {
      const file: File | undefined = event?.target?.files?.[0];
      if (!file) return;
      if (!this.decisionMap[requestId]) {
        this.decisionMap[requestId] = { designUrl: '', note: '', uploading: false };
      }

      this.decisionMap[requestId].uploading = true;
      this.uploadService.uploadImage(file).subscribe({
        next: (url) => {
          this.decisionMap[requestId].designUrl = url;
          this.decisionMap[requestId].uploading = false;
        },
        error: () => {
          this.decisionMap[requestId].uploading = false;
          alert('Design upload failed.');
        }
      });
    }

    getStateBadgeClass(state?: string): string {
      if (state === 'APPROVED') return 'badge-approved';
      if (state === 'REJECTED') return 'badge-rejected';
      return 'badge-pending';
    }
}
