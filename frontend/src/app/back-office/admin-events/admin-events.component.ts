import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../core/services/event.service';
import { Event, EventStatus } from '../../core/models/event.model';

@Component({
    selector: 'app-admin-events',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container mt-4">
      <h2>Global Event Supervision</h2>

      <div class="card mb-4 border-0 shadow-sm">
        <div class="card-body">
          <h5 class="card-title text-muted">Total Events</h5>
          <h2 class="display-6 fw-bold text-primary">{{ events.length }}</h2>
        </div>
      </div>

      <table class="table table-bordered table-striped">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Organizer</th>
            <th>Type</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let ev of events">
            <td>{{ ev.id }}</td>
            <td>{{ ev.title }}</td>
            <td>{{ ev.organizerName || 'Unknown' }}</td>
            <td><span class="badge bg-secondary">{{ ev.type }}</span></td>
            <td>{{ ev.status }}</td>
            <td>
              <button class="btn btn-sm btn-danger" (click)="deleteEvent(ev.id!)">Remove</button>
              <button *ngIf="ev.status !== 'CANCELLED'" class="btn btn-sm btn-warning ms-2" (click)="cancelEvent(ev.id!)">Force Cancel</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class AdminEventsComponent implements OnInit {
    events: Event[] = [];

    constructor(private eventService: EventService) { }

    ngOnInit(): void {
        this.loadEvents();
    }

    loadEvents(): void {
        this.eventService.getAll().subscribe(data => this.events = data);
    }

    deleteEvent(id: number): void {
        if (confirm('Are you sure you want to permanently delete this event?')) {
            this.eventService.delete(id).subscribe(() => this.loadEvents());
        }
    }

    cancelEvent(id: number): void {
        if (confirm('Force cancel this event?')) {
            this.eventService.updateStatus(id, EventStatus.CANCELLED).subscribe(() => this.loadEvents());
        }
    }
}
