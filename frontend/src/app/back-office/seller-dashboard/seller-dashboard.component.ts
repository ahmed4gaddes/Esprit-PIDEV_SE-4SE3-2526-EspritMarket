import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import { LiveSessionService } from '../../core/services/live-session.service';
import { Event, EventType, EventStatus } from '../../core/models/event.model';
import { LiveSession, LivePlatform, LiveSessionStatus } from '../../core/models/live-session.model';
import { TimesManagementComponent } from '../times-management/times-management.component';

@Component({
    selector: 'app-seller-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, TimesManagementComponent],
    template: `
    <div class="container mt-5 pt-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>📊 Seller Area - My Store</h2>
          <div>
            <button class="btn btn-outline-primary me-2" (click)="openEventModal()">+ Product Launch</button>
            <button class="btn btn-outline-danger" (click)="openLiveModal()">+ Promo Live</button>
          </div>
      </div>

      <div class="row">
        <!-- Events Section -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-calendar-event me-2"></i> Related Events (Store)</h5>
                    <span class="badge bg-light text-primary rounded-pill">{{ events.length }}</span>
                </div>
                <div class="card-body">
                    <div *ngIf="events.length === 0" class="text-muted text-center py-3">
                        <p>No events linked to this store yet.</p>
                    </div>
                    <ul class="list-group list-group-flush" *ngIf="events.length > 0">
                        <li class="list-group-item px-0" *ngFor="let event of events">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1 fw-bold">{{ event.title }}</h6>
                                <small class="text-muted">{{ event.date | date:'shortDate' }}</small>
                            </div>
                            <p class="mb-1 text-muted small">{{ event.description || 'No description' }}</p>
                            <span class="badge bg-secondary">{{ event.type }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Live Sessions Section -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-danger text-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-camera-video me-2"></i> Live Sessions (Store)</h5>
                    <span class="badge bg-light text-danger rounded-pill">{{ liveSessions.length }}</span>
                </div>
                <div class="card-body">
                    <div *ngIf="liveSessions.length === 0" class="text-muted text-center py-3">
                        <p>No live session scheduled for this store.</p>
                    </div>
                    <ul class="list-group list-group-flush" *ngIf="liveSessions.length > 0">
                        <li class="list-group-item px-0" *ngFor="let live of liveSessions">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1 fw-bold">{{ live.title }}</h6>
                                <small class="text-muted">{{ live.scheduledAt | date:'shortTime' }}</small>
                            </div>
                            <span class="badge" [ngClass]="{'bg-success': live.status === 'LIVE', 'bg-warning text-dark': live.status === 'SCHEDULED'}">
                                {{ live.status }}
                            </span>
                            <span class="badge bg-dark ms-2">{{ live.platform }}</span>
                            <div class="mt-2">
                              <button class="btn btn-sm btn-outline-dark" (click)="openTimes(live.id!)">
                                <i class="bi bi-clock me-1"></i>Times
                              </button>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
      </div>

      <app-times-management
        *ngIf="showTimesModal && timesLiveId"
        [liveSessionId]="timesLiveId!"
        (closed)="showTimesModal = false"
      ></app-times-management>
      
      <!-- Event Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEventModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Create an Event (Current Store)</h5>
              <button type="button" class="btn-close" (click)="showEventModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Event Title</label>
                <input class="form-control" [(ngModel)]="newEvent.title" placeholder="Ex: New Product Line Launch">
              </div>
              <div class="mb-3">
                <label>Date and Time</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
              </div>
              <div class="mb-3">
                <label>Event Type</label>
                <select class="form-select" [(ngModel)]="newEvent.type">
                  <option value="PRODUCT_LAUNCH_EVENT">Product Launch Event</option>
                  <option value="STORE_ACTIVE_EVENT">Store Active Event</option>
                  <option value="TOP_SELLER_EVENT">Top Seller Event</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showEventModal = false">Cancel</button>
              <button class="btn btn-primary" (click)="createEvent()">Create</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Live Session Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showLiveModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Schedule a Live (Current Store)</h5>
              <button type="button" class="btn-close" (click)="showLiveModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Live Title</label>
                <input class="form-control" [(ngModel)]="newLive.title" placeholder="Ex: Live Product Presentation">
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
                <label>Broadcast Date</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newLive.scheduledAt">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showLiveModal = false">Cancel</button>
              <button class="btn btn-danger" (click)="createLive()">Schedule</button>
            </div>
          </div>
        </div>
      </div>
      
    </div>
  `,
    styles: []
})
export class SellerDashboardComponent implements OnInit {
    events: Event[] = [];
    liveSessions: LiveSession[] = [];
    
    currentStoreId = 1;

    showEventModal = false;
    showLiveModal = false;

    // Times modal state
    showTimesModal = false;
    timesLiveId: number | null = null;
    
    newEvent: Partial<Event> = {
        title: '', description: '', location: 'Store', capacity: 50, ticketPrice: 0,
        type: EventType.PRODUCT_LAUNCH_EVENT, date: new Date(), storeId: this.currentStoreId
    };
    
    newLive: Partial<LiveSession> = {
        title: '', platform: LivePlatform.INSTAGRAM, scheduledAt: new Date(),
        status: LiveSessionStatus.SCHEDULED, storeId: this.currentStoreId
    };

    constructor(
        private eventService: EventService,
        private liveSessionService: LiveSessionService
    ) {}

    ngOnInit(): void {
        this.loadStoreData();
    }

    loadStoreData(): void {
        this.eventService.getByStore(this.currentStoreId).subscribe(data => this.events = data);
        this.liveSessionService.getByStore(this.currentStoreId).subscribe(data => this.liveSessions = data);
    }
    
    openEventModal() {
        this.newEvent.storeId = this.currentStoreId;
        this.showEventModal = true;
    }
    
    openLiveModal() {
        this.newLive.storeId = this.currentStoreId;
        this.showLiveModal = true;
    }
    
    createEvent() {
        this.eventService.create(this.newEvent).subscribe(() => {
            this.showEventModal = false;
            this.loadStoreData();
        });
    }
    
    createLive() {
        this.liveSessionService.create(this.newLive).subscribe((created) => {
            this.showLiveModal = false;
            this.loadStoreData();
            if (created?.id) {
              this.timesLiveId = created.id;
              this.showTimesModal = true;
            }
        });
    }

    openTimes(liveId: number): void {
      this.timesLiveId = liveId;
      this.showTimesModal = true;
    }
}
