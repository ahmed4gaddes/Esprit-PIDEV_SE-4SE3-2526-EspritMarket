import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Rate } from '../../models/rate';
import { RateService } from '../../Services/rate.service';

type Star = 1 | 2 | 3 | 4 | 5;

@Component({
  selector: 'app-user-rate-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal d-block bg-dark bg-opacity-50">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              Rate Seller - {{ ratedUserName || ('User #' + ratedUserId) }}
            </h5>
            <button type="button" class="btn-close" (click)="closed.emit()"></button>
          </div>

          <div class="modal-body">
            <div class="d-flex justify-content-between align-items-center mb-3 gap-3">
              <div>
                <div class="text-muted small">Average rating</div>
                <div class="d-flex align-items-center gap-2">
                  <i class="bi bi-star-fill text-warning"></i>
                  <span class="fw-bold">
                    {{ average > 0 ? (average | number:'1.1-1') : 'No ratings yet' }}
                  </span>
                  <span class="text-muted small" *ngIf="count > 0">({{ count }} review(s))</span>
                </div>
              </div>
              <span class="badge" [ngClass]="myRate ? 'bg-success' : 'bg-secondary'">
                {{ myRate ? 'Already rated' : 'Not rated yet' }}
              </span>
            </div>

            <hr class="my-4" />

            <h6 class="fw-bold mb-3">Write your rating</h6>
            <div class="mb-3">
              <label class="form-label">Star rating</label>
              <div class="d-flex gap-2 flex-wrap">
                <i
                  *ngFor="let s of stars"
                  class="bi me-1"
                  role="button"
                  style="cursor:pointer; font-size: 1.8rem;"
                  [ngClass]="s <= formStar ? 'bi-star-fill text-warning' : 'bi-star text-warning opacity-50'"
                  (click)="formStar = s"
                ></i>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Comment (optional)</label>
              <textarea
                class="form-control"
                rows="3"
                [(ngModel)]="formComment"
                name="comment"
                placeholder="Write a short comment..."
              ></textarea>
            </div>

            <div class="d-flex justify-content-end gap-2">
              <button class="btn btn-outline-secondary" type="button" (click)="closed.emit()">Cancel</button>
              <button class="btn btn-primary" type="button" [disabled]="submitting" (click)="submit()">
                {{ myRate ? 'Update rating' : 'Submit rating' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class UserRateModalComponent implements OnChanges {
  @Input({ required: true }) ratedUserId!: number;
  @Input({ required: true }) currentUserId!: number;
  @Input() ratedUserName?: string;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  stars: Star[] = [1, 2, 3, 4, 5];
  rates: Rate[] = [];
  myRate: Rate | null = null;
  average = 0;
  count = 0;
  submitting = false;

  formStar: Star = 5;
  formComment = '';

  constructor(private rateService: RateService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ratedUserId'] || changes['currentUserId']) && this.ratedUserId && this.currentUserId) {
      this.load();
    }
  }

  private load(): void {
    this.rateService.getRatesForUser(this.ratedUserId).subscribe({
      next: (data) => {
        this.rates = data || [];
        this.count = this.rates.length;
        const stars = this.rates.map(r => Number(r.star || 0));
        const sum = stars.reduce((a, b) => a + b, 0);
        this.average = stars.length ? (sum / stars.length) : 0;

        this.myRate = this.rates.find(r => this.getRaterId(r) === this.currentUserId) || null;
        if (this.myRate) {
          this.formStar = (this.myRate.star as Star) || 5;
          this.formComment = this.myRate.comment || '';
        } else {
          this.formStar = 5;
          this.formComment = '';
        }
      },
      error: (err) => {
        console.error('Failed to load rates', err);
        this.rates = [];
        this.count = 0;
        this.average = 0;
        this.myRate = null;
      }
    });
  }

  private getRaterId(r: Rate): number | null {
    return r.raterId ?? null;
  }

  submit(): void {
    if (!this.ratedUserId || !this.currentUserId) return;
    const payload: Rate = { star: this.formStar, comment: this.formComment || '' };
    this.submitting = true;
    const obs = this.myRate?.id
      ? this.rateService.update(this.myRate.id, payload)
      : this.rateService.addRate(this.ratedUserId, payload);

    obs.subscribe({
      next: () => {
        this.submitting = false;
        this.load();
        this.saved.emit();
      },
      error: (err) => {
        this.submitting = false;
        console.error('Failed to submit rate', err);
        alert(err?.error?.message || 'Failed to submit rate');
      }
    });
  }
}

