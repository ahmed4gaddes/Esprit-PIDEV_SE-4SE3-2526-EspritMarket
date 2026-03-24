import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LiveSessionService } from '../../core/services/live-session.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';

@Component({
    selector: 'app-admin-lives',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container mt-4">
      <h2>Global Live Sessions Supervision</h2>

      <table class="table table-bordered table-striped mt-4">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Creator</th>
            <th>Platform</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let live of lives">
            <td>{{ live.id }}</td>
            <td>{{ live.title }}</td>
            <td>{{ live.creatorName || 'Unknown' }}</td>
            <td><span class="badge bg-secondary">{{ live.platform }}</span></td>
            <td>
              <span class="badge" [ngClass]="{'bg-danger pulse': live.status === 'LIVE', 'bg-dark': live.status !== 'LIVE'}">{{ live.status }}</span>
            </td>
            <td>
              <button class="btn btn-sm btn-danger" (click)="deleteLive(live.id!)">Remove</button>
              <button *ngIf="live.status === 'LIVE'" class="btn btn-sm btn-warning ms-2" (click)="forceEnd(live.id!)">Force End</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class AdminLivesComponent implements OnInit {
    lives: LiveSession[] = [];

    constructor(private liveSessionService: LiveSessionService) { }

    ngOnInit(): void {
        this.loadLives();
    }

    loadLives(): void {
        this.liveSessionService.getAll().subscribe(data => this.lives = data);
    }

    deleteLive(id: number): void {
        if (confirm('Permanently delete this live session?')) {
            this.liveSessionService.delete(id).subscribe(() => this.loadLives());
        }
    }

    forceEnd(id: number): void {
        if (confirm('Force end this live session?')) {
            this.liveSessionService.updateStatus(id, LiveSessionStatus.ENDED).subscribe(() => this.loadLives());
        }
    }
}
