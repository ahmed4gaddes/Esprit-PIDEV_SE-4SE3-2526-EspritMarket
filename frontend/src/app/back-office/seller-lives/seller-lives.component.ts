import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LiveSessionService } from '../../core/services/live-session.service';
import { LiveSession, LiveSessionStatus, LivePlatform } from '../../core/models/live-session.model';

@Component({
    selector: 'app-seller-lives',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="container mt-4">
      <h2>My Live Sessions</h2>
      <button class="btn btn-primary mb-3" (click)="showCreateModal = true">Create New Live</button>

      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>Title</th>
            <th>Platform</th>
            <th>Status</th>
            <th>Scheduled At</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let live of lives">
            <td>{{ live.title }}</td>
            <td>{{ live.platform }}</td>
            <td><span class="badge" [ngClass]="{'bg-danger': live.status === 'LIVE', 'bg-secondary': live.status !== 'LIVE'}">{{ live.status }}</span></td>
            <td>{{ live.scheduledAt | date:'short' }}</td>
            <td>
              <button *ngIf="live.status === 'SCHEDULED'" class="btn btn-sm btn-success me-2" (click)="updateStatus(live.id!, 'LIVE')">Start</button>
              <button *ngIf="live.status === 'LIVE'" class="btn btn-sm btn-warning me-2" (click)="updateStatus(live.id!, 'ENDED')">End</button>
              <button class="btn btn-sm btn-danger" (click)="deleteLive(live.id!)">Delete</button>
            </td>
          </tr>
          <tr *ngIf="lives.length === 0 && !loading">
            <td colspan="5" class="text-center">No live sessions found.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Quick Modal Simulation for Creation -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showCreateModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Live Session</h5>
            <button type="button" class="btn-close" (click)="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label>Title</label>
              <input type="text" class="form-control" [(ngModel)]="newLive.title">
            </div>
            <div class="mb-3">
              <label>Link</label>
              <input type="text" class="form-control" [(ngModel)]="newLive.link">
            </div>
            <div class="mb-3">
              <label>Platform</label>
              <select class="form-select" [(ngModel)]="newLive.platform">
                <option value="TIKTOK">TikTok</option>
                <option value="INSTAGRAM">Instagram</option>
                <option value="YOUTUBE">YouTube</option>
              </select>
            </div>
            <div class="mb-3">
              <label>Scheduled Date</label>
              <input type="datetime-local" class="form-control" [(ngModel)]="newLive.scheduledAt">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showCreateModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" (click)="createLive()">Create</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SellerLivesComponent implements OnInit {
    lives: LiveSession[] = [];
    loading = true;
    showCreateModal = false;

    newLive: Partial<LiveSession> = {
        title: '',
        link: '',
        platform: LivePlatform.TIKTOK,
        scheduledAt: new Date()
    };

    constructor(private liveSessionService: LiveSessionService) { }

    ngOnInit(): void {
        this.loadLives();
    }

    loadLives(): void {
        // Hardcoded User ID 1 for mock
        this.liveSessionService.getByCreator(1).subscribe(data => {
            this.lives = data;
            this.loading = false;
        });
    }

    createLive(): void {
        this.liveSessionService.create(this.newLive).subscribe(() => {
            this.showCreateModal = false;
            this.loadLives();
            this.newLive = { title: '', link: '', platform: LivePlatform.TIKTOK, scheduledAt: new Date() };
        });
    }

    updateStatus(id: number, statusStr: string): void {
        const status = statusStr as LiveSessionStatus;
        this.liveSessionService.updateStatus(id, status).subscribe(() => this.loadLives());
    }

    deleteLive(id: number): void {
        if (confirm('Are you sure you want to delete this live session?')) {
            this.liveSessionService.delete(id).subscribe(() => this.loadLives());
        }
    }
}
