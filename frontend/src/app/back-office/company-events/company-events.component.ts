import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import { Event, EventType, EventStatus } from '../../core/models/event.model';

@Component({
    selector: 'app-company-events',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="container mt-4">
      <h2>Company Events Management</h2>
      <button class="btn btn-primary mb-3" (click)="showCreateModal = true">Create New Event</button>

      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>Title</th>
            <th>Type</th>
            <th>Date</th>
            <th>Capacity</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let event of events">
            <td>{{ event.title }}</td>
            <td><span class="badge bg-info">{{ event.type }}</span></td>
            <td>{{ event.date | date:'shortDate' }}</td>
            <td>{{ event.ticketCount || 0 }} / {{ event.capacity }}</td>
            <td><span class="badge" [ngClass]="{'bg-success': event.status === 'UPCOMING', 'bg-warning': event.status === 'ONGOING'}">{{ event.status }}</span></td>
            <td>
              <button *ngIf="event.status === 'UPCOMING'" class="btn btn-sm btn-warning me-2" (click)="updateStatus(event.id!, 'ONGOING')">Start</button>
              <button *ngIf="event.status === 'ONGOING'" class="btn btn-sm btn-secondary me-2" (click)="updateStatus(event.id!, 'COMPLETED')">Complete</button>
              <button class="btn btn-sm btn-danger" (click)="deleteEvent(event.id!)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Quick Modal Simulation for Creation -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showCreateModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Company Event</h5>
            <button type="button" class="btn-close" (click)="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label>Title</label>
              <input type="text" class="form-control" [(ngModel)]="newEvent.title">
            </div>
            <div class="mb-3">
              <label>Location</label>
              <input type="text" class="form-control" [(ngModel)]="newEvent.location">
            </div>
            <div class="mb-3">
              <label>Type</label>
              <select class="form-select" [(ngModel)]="newEvent.type">
                <option value="COMPANY_INTERNSHIP_EVENT">Internship Event</option>
                <option value="NETWORKING_EVENT">Networking Event</option>
              </select>
            </div>
             <div class="mb-3">
              <label>Capacity</label>
              <input type="number" class="form-control" [(ngModel)]="newEvent.capacity">
            </div>
             <div class="mb-3">
              <label>Ticket Price ($)</label>
              <input type="number" class="form-control" [(ngModel)]="newEvent.ticketPrice">
            </div>
            <div class="mb-3">
              <label>Date</label>
              <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showCreateModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" (click)="createEvent()">Create</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CompanyEventsComponent implements OnInit {
    events: Event[] = [];
    showCreateModal = false;

    newEvent: Partial<Event> = {
        title: '',
        location: '',
        type: EventType.COMPANY_INTERNSHIP_EVENT,
        capacity: 50,
        ticketPrice: 0,
        date: new Date()
    };

    constructor(private eventService: EventService) { }

    ngOnInit(): void {
        this.loadEvents();
    }

    loadEvents(): void {
        // Ideally filter by company ID, for now get all
        this.eventService.getAll().subscribe(data => this.events = data);
    }

    createEvent(): void {
        this.eventService.create(this.newEvent).subscribe(() => {
            this.showCreateModal = false;
            this.loadEvents();
        });
    }

    updateStatus(id: number, statusStr: string): void {
        const status = statusStr as EventStatus;
        this.eventService.updateStatus(id, status).subscribe(() => this.loadEvents());
    }

    deleteEvent(id: number): void {
        if (confirm('Delete this event?')) {
            this.eventService.delete(id).subscribe(() => this.loadEvents());
        }
    }
}
