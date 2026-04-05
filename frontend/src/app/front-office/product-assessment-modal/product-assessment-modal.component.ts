import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductAssessment } from '../../models/product-assessment';
import { ProductAssessmentService } from '../../Services/product-assessment.service';

type Star = 1 | 2 | 3 | 4 | 5;

@Component({
  selector: 'app-product-assessment-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal d-block bg-dark bg-opacity-50">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              Product Rating - Product #{{ productId }}
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
                    {{ averageStar > 0 ? (averageStar | number:'1.1-1') : 'No ratings yet' }}
                  </span>
                  <span class="text-muted small" *ngIf="ratingsCount > 0">
                    ({{ ratingsCount }} review(s))
                  </span>
                </div>
              </div>
              <div class="text-end">
                <div class="text-muted small">Your status</div>
                <span class="badge" [ngClass]="myAssessment ? 'bg-success' : 'bg-secondary'">
                  {{ myAssessment ? 'Already reviewed' : 'Not reviewed yet' }}
                </span>
              </div>
            </div>

            <hr class="my-4" />

            <h6 class="fw-bold mb-3">Write your review</h6>

            <div class="mb-3">
              <label class="form-label">Star rating</label>
              <div class="d-flex gap-2 flex-wrap">
                <i
                  *ngFor="let s of stars"
                  class="bi me-1"
                  role="button"
                  style="cursor: pointer; font-size: 1.8rem;"
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
              <button type="button" class="btn btn-outline-secondary" (click)="closed.emit()">
                Cancel
              </button>
              <button
                type="button"
                class="btn btn-primary"
                [disabled]="submitting || formStar < 1 || formStar > 5"
                (click)="submit()"
              >
                <span *ngIf="!submitting">
                  {{ myAssessment ? 'Update review' : 'Submit review' }}
                </span>
                <span *ngIf="submitting" class="d-flex align-items-center gap-2">
                  <span class="spinner-border spinner-border-sm"></span> Saving...
                </span>
              </button>
            </div>

            <hr class="my-4" />

            <h6 class="fw-bold mb-3">All reviews</h6>

            <div *ngIf="loadingReviews" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
              <div class="text-muted small mt-2">Loading reviews...</div>
            </div>

            <div *ngIf="!loadingReviews && assessments.length === 0" class="text-center py-4">
              <div class="text-muted">No review yet.</div>
            </div>

            <div *ngIf="!loadingReviews && assessments.length > 0" class="list-group">
              <div class="list-group-item d-flex justify-content-between align-items-start gap-3"
                   *ngFor="let a of assessments">
                <div>
                  <div class="d-flex align-items-center gap-2 mb-1">
                    <i
                      *ngFor="let s of stars"
                      class="bi me-1"
                      [ngClass]="s <= (a.star || 0) ? 'bi-star-fill text-warning' : 'bi-star text-warning opacity-50'"
                    ></i>
                    <span class="fw-bold">
                      {{ a.star }}/5
                    </span>
                    <span class="badge bg-light text-dark border ms-2 small"
                          *ngIf="getUserId(a) === currentUserId">
                      You
                    </span>
                  </div>
                  <div class="small text-muted" *ngIf="a.comment">
                    {{ a.comment }}
                  </div>
                  <div class="small text-secondary mt-2" *ngIf="a.date">
                    {{ a.date | date:'short' }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closed.emit()">Close</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .modal-content {
        background: #fff;
      }
    `
  ]
})
export class ProductAssessmentModalComponent implements OnChanges {
  @Input({ required: true }) productId!: number;
  @Input({ required: true }) currentUserId!: number;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  stars: Star[] = [1, 2, 3, 4, 5];

  assessments: ProductAssessment[] = [];
  loadingReviews = false;
  submitting = false;

  formStar: Star = 5;
  formComment = '';

  myAssessment: ProductAssessment | null = null;

  averageStar = 0;
  ratingsCount = 0;

  constructor(private productAssessmentService: ProductAssessmentService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['productId'] || changes['currentUserId']) {
      if (this.productId && this.currentUserId) {
        this.loadAssessments();
      }
    }
  }

  loadAssessments(): void {
    this.loadingReviews = true;
    this.productAssessmentService.getByProduct(this.productId).subscribe({
      next: (data) => {
        this.assessments = data || [];
        this.ratingsCount = this.assessments.length;

        const stars = this.assessments.map(a => Number(a.star || 0)).filter(n => n >= 0);
        const sum = stars.reduce((acc, v) => acc + v, 0);
        this.averageStar = stars.length ? sum / stars.length : 0;

        this.myAssessment = this.assessments.find(a => this.getUserId(a) === this.currentUserId) || null;
        if (this.myAssessment) {
          this.formStar = (this.myAssessment.star as Star) || 5;
          this.formComment = this.myAssessment.comment || '';
        } else {
          this.formStar = 5;
          this.formComment = '';
        }

        this.loadingReviews = false;
      },
      error: (err) => {
        console.error('Failed to load product assessments', err);
        this.assessments = [];
        this.averageStar = 0;
        this.ratingsCount = 0;
        this.myAssessment = null;
        this.formStar = 5;
        this.formComment = '';
        this.loadingReviews = false;
      }
    });
  }

  getUserId(a: ProductAssessment): number | null {
    if (typeof a.userId === 'number') return a.userId;
    if (a.user?.id != null) return a.user.id;
    return null;
  }

  submit(): void {
    if (!this.productId || !this.currentUserId) return;

    const payload: ProductAssessment = {
      star: this.formStar,
      comment: this.formComment || ''
    };

    this.submitting = true;

    const obs = this.myAssessment?.id
      ? this.productAssessmentService.update(this.myAssessment.id, payload)
      : this.productAssessmentService.addAssessment(this.productId, payload);

    obs.subscribe({
      next: () => {
        this.submitting = false;
        this.loadAssessments();
        this.saved.emit();
      },
      error: (err) => {
        this.submitting = false;
        console.error('Failed to submit review', err);
        alert(err?.error?.message || 'Failed to submit review');
      }
    });
  }
}

