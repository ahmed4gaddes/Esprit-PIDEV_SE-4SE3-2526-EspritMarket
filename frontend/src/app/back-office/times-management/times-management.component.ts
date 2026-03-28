import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Times } from '../../models/times';
import { TimesService } from '../../Services/times.service';

type TimesType = 'WORKSHOP' | 'Q&A' | 'DEMO' | 'TRAINING' | 'OTHER';

@Component({
  selector: 'app-times-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal d-block bg-dark bg-opacity-50">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              Manage Times for Live #{{ liveSessionId }}
            </h5>
            <button type="button" class="btn-close" (click)="closed.emit()"></button>
          </div>

          <div class="modal-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div>
                <span class="badge bg-secondary">
                  {{ times.length }} slot(s)
                </span>
              </div>
              <button class="btn btn-outline-secondary btn-sm" (click)="loadTimes()" [disabled]="loading">
                <i class="bi bi-arrow-clockwise me-1"></i> Refresh
              </button>
            </div>

            <hr class="my-4" />

            <h6 class="fw-bold mb-3" *ngIf="!editId">Add new time slot</h6>
            <h6 class="fw-bold mb-3" *ngIf="editId">Edit time slot #{{ editId }}</h6>

            <form (ngSubmit)="editId ? updateTime() : addTime()" #timesForm="ngForm">
              <div class="row g-3">
                <div class="col-md-4">
                  <label class="form-label">Type</label>
                  <select class="form-select" [(ngModel)]="formType" name="type" required>
                    <option *ngFor="let t of timesTypes" [value]="t">{{ t }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label">Start Time</label>
                  <input
                    type="datetime-local"
                    class="form-control"
                    [(ngModel)]="formStart"
                    name="startTime"
                    required
                  />
                </div>

                <div class="col-md-4">
                  <label class="form-label">End Time</label>
                  <input
                    type="datetime-local"
                    class="form-control"
                    [(ngModel)]="formEnd"
                    name="endTime"
                    required
                  />
                </div>

                <div class="col-md-12">
                  <label class="form-label">Description (optional)</label>
                  <textarea
                    class="form-control"
                    rows="2"
                    [(ngModel)]="formDescription"
                    name="description"
                  ></textarea>
                </div>

                <div class="col-md-12">
                  <div class="alert alert-info py-2 mb-0" *ngIf="durationMinutes > 0">
                    Duration: <strong>{{ durationMinutes }}</strong> minute(s)
                  </div>
                  <div class="alert alert-danger py-2 mb-0" *ngIf="durationMinutes === 0">
                    End time must be after start time.
                  </div>
                </div>

                <div class="col-md-12 d-flex justify-content-end gap-2 mt-2">
                  <button type="button" class="btn btn-outline-secondary" (click)="resetForm()" *ngIf="editId">
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="btn btn-primary"
                    [disabled]="loading || durationMinutes <= 0 || !formStart || !formEnd || !formType"
                  >
                    <i class="bi" [ngClass]="editId ? 'bi-pencil' : 'bi-plus-circle'"></i>
                    {{ editId ? 'Update Time' : 'Add Time' }}
                  </button>
                </div>
              </div>
            </form>

            <hr class="my-4" />

            <h6 class="fw-bold mb-3">Existing times</h6>
            <div *ngIf="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
              <div class="text-muted mt-2">Loading times...</div>
            </div>

            <div *ngIf="!loading && times.length === 0" class="text-center py-4">
              <i class="bi bi-clock-history fs-1 text-muted opacity-25"></i>
              <p class="mt-3 mb-0 text-muted">No time slot yet.</p>
            </div>

            <div class="list-group" *ngIf="!loading && times.length > 0">
              <div
                class="list-group-item d-flex justify-content-between align-items-start gap-3"
                *ngFor="let t of times"
              >
                <div class="flex-grow-1">
                  <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-light text-dark border">{{ t.type }}</span>
                    <small class="text-muted">{{ t.startTime | date:'short' }}</small>
                    <small class="text-muted">→</small>
                    <small class="text-muted" *ngIf="t.endTime">{{ t.endTime | date:'short' }}</small>
                  </div>

                  <div class="mt-2">
                    <div class="text-muted small">
                      Duration: <strong>{{ t.duration }}</strong> minute(s)
                    </div>
                    <div class="small text-dark" *ngIf="t.description">
                      {{ t.description }}
                    </div>
                  </div>
                </div>

                <div class="d-flex flex-column gap-2">
                  <button class="btn btn-sm btn-outline-primary" type="button" (click)="startEdit(t)">
                    <i class="bi bi-pencil me-1"></i>Edit
                  </button>
                  <button class="btn btn-sm btn-outline-danger" type="button" (click)="deleteTime(t)">
                    <i class="bi bi-trash me-1"></i>Delete
                  </button>
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
  `
})
export class TimesManagementComponent implements OnChanges {
  @Input({ required: true }) liveSessionId!: number;
  @Output() closed = new EventEmitter<void>();

  timesTypes: TimesType[] = ['WORKSHOP', 'Q&A', 'DEMO', 'TRAINING', 'OTHER'];
  times: Times[] = [];
  loading = false;

  // Form state (Option A: Start + End)
  formType: TimesType = 'WORKSHOP';
  formStart = '';
  formEnd = '';
  formDescription = '';

  editId: number | null = null;

  get durationMinutes(): number {
    if (!this.formStart || !this.formEnd) return 0;
    const start = new Date(this.formStart);
    const end = new Date(this.formEnd);
    const diffMs = end.getTime() - start.getTime();
    if (!Number.isFinite(diffMs) || diffMs <= 0) return 0;
    return Math.round(diffMs / 60000);
  }

  constructor(private timesService: TimesService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['liveSessionId'] && this.liveSessionId) {
      this.resetForm();
      this.loadTimes();
    }
  }

  loadTimes(): void {
    if (!this.liveSessionId) return;
    this.loading = true;
    this.timesService.getByLiveSession(this.liveSessionId).subscribe({
      next: (data) => {
        this.times = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load times', err);
        this.times = [];
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.editId = null;
    this.formType = 'WORKSHOP';
    this.formStart = '';
    this.formEnd = '';
    this.formDescription = '';
  }

  startEdit(t: Times): void {
    if (!t.id) return;
    this.editId = t.id;
    this.formType = (t.type as TimesType) || 'WORKSHOP';
    this.formStart = this.toDatetimeLocal(t.startTime);
    this.formEnd = this.toDatetimeLocal(t.endTime);
    this.formDescription = t.description || '';
  }

  addTime(): void {
    if (this.durationMinutes <= 0 || !this.formStart || !this.formEnd) return;
    const start = new Date(this.formStart);
    const end = new Date(this.formEnd);

    const payload: Times = {
      type: this.formType,
      startTime: start.toISOString(),
      endTime: end.toISOString(),
      duration: this.durationMinutes,
      description: this.formDescription
    };

    this.loading = true;
    this.timesService.addTimes(this.liveSessionId, payload).subscribe({
      next: () => {
        this.loading = false;
        this.resetForm();
        this.loadTimes();
      },
      error: (err) => {
        console.error('Failed to add time', err);
        this.loading = false;
        alert(err?.error?.message || 'Failed to add time');
      }
    });
  }

  updateTime(): void {
    if (!this.editId || this.durationMinutes <= 0 || !this.formStart || !this.formEnd) return;
    const start = new Date(this.formStart);
    const end = new Date(this.formEnd);

    const payload: Times = {
      type: this.formType,
      startTime: start.toISOString(),
      endTime: end.toISOString(),
      duration: this.durationMinutes,
      description: this.formDescription
    };

    this.loading = true;
    this.timesService.update(this.editId, payload).subscribe({
      next: () => {
        this.loading = false;
        this.resetForm();
        this.loadTimes();
      },
      error: (err) => {
        console.error('Failed to update time', err);
        this.loading = false;
        alert(err?.error?.message || 'Failed to update time');
      }
    });
  }

  deleteTime(t: Times): void {
    if (!t.id) return;
    if (!confirm('Delete this time slot?')) return;
    this.loading = true;
    this.timesService.delete(t.id).subscribe({
      next: () => {
        this.loading = false;
        this.loadTimes();
      },
      error: (err) => {
        console.error('Failed to delete time', err);
        this.loading = false;
        alert(err?.error?.message || 'Failed to delete time');
      }
    });
  }

  private toDatetimeLocal(value: any): string {
    if (!value) return '';
    const d = new Date(value);
    if (!Number.isFinite(d.getTime())) return '';

    // datetime-local expects local time, so we remove timezone offset from UTC value
    d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
    return d.toISOString().slice(0, 16);
  }
}

