import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LiveSessionService } from '../../core/services/live-session.service';
import { EventService } from '../../core/services/event.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { UploadService } from '../../core/services/upload.service';
import { LiveSession, LiveSessionStatus, LivePlatform } from '../../core/models/live-session.model';
import { Event, EventStatus, EventType } from '../../core/models/event.model';

@Component({
  selector: 'app-seller-lives',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container mt-4">
      <h2>Seller Dashboard</h2>

      <!-- Store Selector -->
      <div class="card mb-4 border-primary">
        <div class="card-body d-flex align-items-center gap-3">
          <i class="bi bi-shop fs-4 text-primary"></i>
          <div class="flex-grow-1">
            <label class="form-label mb-1 fw-bold">Select Your Store</label>
            <select class="form-select" [(ngModel)]="selectedStoreId" (ngModelChange)="onStoreChanged()">
              <option [ngValue]="null">-- Choose your store --</option>
              <option *ngFor="let store of stores" [ngValue]="store.id">{{ store.name }}</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Message when no store selected -->
      <div class="alert alert-warning" *ngIf="!selectedStoreId">
        <i class="bi bi-exclamation-triangle me-2"></i>
        Please select your store above to manage your live sessions and product launch events.
      </div>

      <!-- Content only visible when a store is selected -->
      <div *ngIf="selectedStoreId">
        <!-- Tabs Navigation -->
        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <a class="nav-link" [class.active]="activeTab === 'lives'" (click)="activeTab = 'lives'" style="cursor:pointer">
              <i class="bi bi-broadcast me-2 text-danger"></i>My Live Sessions
              <span class="badge bg-danger ms-1">{{ lives.length }}</span>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" [class.active]="activeTab === 'events'" (click)="activeTab = 'events'" style="cursor:pointer">
              <i class="bi bi-calendar-star me-2 text-primary"></i>My Product Launches
              <span class="badge bg-primary ms-1">{{ events.length }}</span>
            </a>
          </li>
        </ul>

        <!-- LIVES TAB -->
        <div *ngIf="activeTab === 'lives'">
          <button class="btn btn-primary mb-3" (click)="showCreateLiveModal = true">
            <i class="bi bi-plus-circle me-1"></i> Create New Live
          </button>

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
                <td><span class="badge" [ngClass]="{'bg-danger': live.status === 'LIVE', 'bg-success': live.status === 'SCHEDULED', 'bg-secondary': live.status === 'ENDED' || live.status === 'CANCELLED'}">{{ live.status }}</span></td>
                <td>{{ live.scheduledAt | date:'short' }}</td>
                <td>
                  <button *ngIf="live.status === 'SCHEDULED'" class="btn btn-sm btn-success me-2" (click)="updateLiveStatus(live.id!, 'LIVE')">Start</button>
                  <button *ngIf="live.status === 'LIVE'" class="btn btn-sm btn-warning me-2" (click)="updateLiveStatus(live.id!, 'ENDED')">End</button>
                  <a *ngIf="live.status === 'LIVE' && live.platform === 'LOCAL'" class="btn btn-sm btn-primary me-2" [routerLink]="'/live/local/' + live.id"><i class="bi bi-chat-text"></i> Join Chat</a>
                  <button class="btn btn-sm btn-danger" (click)="deleteLive(live.id!)"><i class="bi bi-trash"></i></button>
                </td>
              </tr>
              <tr *ngIf="lives.length === 0 && !loadingLives">
                <td colspan="5" class="text-center py-4">
                  <i class="bi bi-broadcast fs-1 text-muted d-block mb-2"></i>
                  No live sessions for this store yet. Click "Create New Live" to get started!
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- EVENTS TAB -->
        <div *ngIf="activeTab === 'events'">
          <button class="btn btn-primary mb-3" (click)="showCreateEventModal = true">
            <i class="bi bi-plus-circle me-1"></i> Create Product Launch Event
          </button>

          <table class="table table-striped table-hover">
            <thead class="table-dark">
              <tr>
                <th>Image</th>
                <th>Title</th>
                <th>Status</th>
                <th>Date</th>
                <th>Capacity</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let event of events">
                <td>
                  <img [src]="event.imageUrl || 'assets/placeholder-event.png'" alt="Event" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                </td>
                <td class="align-middle">{{ event.title }}</td>
                <td class="align-middle">
                  <span class="badge" [ngClass]="{
                    'bg-success': event.status === 'UPCOMING',
                    'bg-primary': event.status === 'ONGOING',
                    'bg-secondary': event.status === 'COMPLETED' || event.status === 'CANCELLED'
                  }">{{ event.status }}</span>
                </td>
                <td class="align-middle">{{ event.date | date:'short' }}</td>
                <td class="align-middle">{{ event.ticketCount || 0 }} / {{ event.capacity }}</td>
                <td class="align-middle">
                  <button *ngIf="event.status === 'UPCOMING'" class="btn btn-sm btn-primary me-1" (click)="updateEventStatus(event.id!, 'ONGOING')">Start</button>
                  <button *ngIf="event.status === 'ONGOING'" class="btn btn-sm btn-warning me-1" (click)="updateEventStatus(event.id!, 'COMPLETED')">Complete</button>
                  <button class="btn btn-sm btn-danger" (click)="deleteEvent(event.id!)"><i class="bi bi-trash"></i></button>
                </td>
              </tr>
              <tr *ngIf="events.length === 0 && !loadingEvents">
                <td colspan="6" class="text-center py-4">
                  <i class="bi bi-calendar-star fs-1 text-muted d-block mb-2"></i>
                  No product launch events for this store yet. Click "Create Product Launch Event" to get started!
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Live Session Creation Modal -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showCreateLiveModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Live Session</h5>
            <button type="button" class="btn-close" (click)="showCreateLiveModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label>Title <span class="text-danger">*</span></label>
              <input type="text" class="form-control" [(ngModel)]="newLive.title">
            </div>
            <div class="mb-3" *ngIf="newLive.platform !== 'LOCAL'">
              <label>Link</label>
              <input type="text" class="form-control" [(ngModel)]="newLive.link" placeholder="https://youtube.com/live/...">
            </div>
            <div class="mb-3">
              <label>Platform</label>
              <select class="form-select" [(ngModel)]="newLive.platform">
                <option value="TIKTOK">TikTok</option>
                <option value="INSTAGRAM">Instagram</option>
                <option value="YOUTUBE">YouTube</option>
                <option value="ZOOM">Zoom</option>
                <option value="GOOGLE_MEET">Google Meet</option>
                <option value="LOCAL">LOCAL (On-site Chat)</option>
              </select>
            </div>
            <div class="mb-3">
              <label>Scheduled Date <span class="text-danger">*</span></label>
              <input type="datetime-local" class="form-control" [(ngModel)]="newLive.scheduledAt">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showCreateLiveModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" (click)="createLive()" [disabled]="!newLive.title">Create Live</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Event Creation Modal -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showCreateEventModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Product Launch Event</h5>
            <button type="button" class="btn-close" (click)="showCreateEventModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label>Title <span class="text-danger">*</span></label>
                <input type="text" class="form-control" [(ngModel)]="newEvent.title" placeholder="e.g., Summer Collection Launch">
              </div>
              <div class="col-md-6 mb-3">
                <label>Date & Time <span class="text-danger">*</span></label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
              </div>
            </div>
            <div class="mb-3">
              <label>Description</label>
              <textarea class="form-control" rows="3" [(ngModel)]="newEvent.description" placeholder="Tell customers about your product launch..."></textarea>
            </div>
            <div class="row">
              <div class="col-md-8 mb-3">
                <label>Location</label>
                <input type="text" class="form-control" [(ngModel)]="newEvent.location" placeholder="e.g., Main Store or Online">
              </div>
              <div class="col-md-4 mb-3">
                <label>Capacity <span class="text-danger">*</span></label>
                <input type="number" class="form-control" [(ngModel)]="newEvent.capacity" min="1">
              </div>
            </div>
            <div class="mb-3">
              <label>Event Image <span class="text-muted small">(poster / banner)</span></label>
              <input type="file" class="form-control" accept="image/*" (change)="onImageSelected($event)">
              <div *ngIf="uploading" class="mt-2 text-primary">
                <span class="spinner-border spinner-border-sm me-1"></span> Uploading...
              </div>
              <div *ngIf="imagePreview && !uploading" class="mt-2">
                <img [src]="imagePreview" alt="Preview" style="max-width: 200px; max-height: 120px; border-radius: 8px; border: 2px solid #ddd;">
                <button class="btn btn-sm btn-outline-danger ms-2" (click)="removeImage()"><i class="bi bi-x"></i> Remove</button>
              </div>
            </div>

            <div class="alert alert-info py-2 mb-0">
              <i class="bi bi-info-circle me-2"></i> This event will be linked to your selected store and categorized as a <strong>Product Launch Event</strong>.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showCreateEventModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" (click)="createEvent()" [disabled]="!newEvent.title || !newEvent.date || !newEvent.capacity">Create Event</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SellerLivesComponent implements OnInit {
  activeTab: 'lives' | 'events' = 'lives';
  selectedStoreId: number | null = null;
  stores: any[] = [];

  // Lives state
  lives: LiveSession[] = [];
  loadingLives = false;
  showCreateLiveModal = false;
  newLive: Partial<LiveSession> = {
    title: '', link: '', platform: LivePlatform.TIKTOK, scheduledAt: new Date()
  };

  // Events state
  events: Event[] = [];
  loadingEvents = false;
  showCreateEventModal = false;
  newEvent: Partial<Event> = {
    title: '', description: '', location: '', imageUrl: '',
    capacity: 100, ticketPrice: 0,
    type: EventType.PRODUCT_LAUNCH_EVENT,
    date: new Date()
  };

  // Image upload state
  imagePreview: string | null = null;
  uploading = false;
  selectedFile: File | null = null;

  constructor(
    private liveSessionService: LiveSessionService,
    private eventService: EventService,
    private storeService: StoreServiceService,
    private uploadService: UploadService
  ) { }

  ngOnInit(): void {
    // Load the seller's stores for the dropdown
    this.storeService.getMyStores().subscribe(data => {
      this.stores = data;
    });
  }

  // Called when the seller selects a store
  onStoreChanged(): void {
    if (this.selectedStoreId) {
      this.loadLives();
      this.loadEvents();
    } else {
      this.lives = [];
      this.events = [];
    }
  }

  // --- LIVES LOGIC ---
  loadLives(): void {
    if (!this.selectedStoreId) return;
    this.loadingLives = true;
    this.liveSessionService.getByStore(this.selectedStoreId).subscribe(data => {
      this.lives = data;
      this.loadingLives = false;
    });
  }

  createLive(): void {
    // Convert string from input datetime-local into a real Date object
    let livePayload = { ...this.newLive };
    if (livePayload.scheduledAt && typeof livePayload.scheduledAt === 'string') {
      livePayload.scheduledAt = new Date(livePayload.scheduledAt);
    }

    // Automatically link to the selected store
    livePayload.storeId = this.selectedStoreId!;
    
    this.liveSessionService.create(livePayload).subscribe({
      next: () => {
        this.showCreateLiveModal = false;
        this.loadLives();
        this.newLive = { title: '', link: '', platform: LivePlatform.TIKTOK, scheduledAt: new Date() };
        alert("Live créé avec succès !");
      },
      error: (err) => {
        console.error("Create Live Error:", err);
        alert("Échec de la création : " + (err.error?.message || err.message));
      }
    });
  }

  updateLiveStatus(id: number, statusStr: string): void {
    const status = statusStr as LiveSessionStatus;
    this.liveSessionService.updateStatus(id, status).subscribe(() => this.loadLives());
  }

  deleteLive(id: number): void {
    if (confirm('Are you sure you want to delete this live session?')) {
      this.liveSessionService.delete(id).subscribe(() => this.loadLives());
    }
  }

  // --- EVENTS LOGIC ---
  loadEvents(): void {
    if (!this.selectedStoreId) return;
    this.loadingEvents = true;
    this.eventService.getByStore(this.selectedStoreId).subscribe(data => {
      this.events = data;
      this.loadingEvents = false;
    });
  }

  createEvent(): void {
    // Automatically set the type and link to the selected store
    this.newEvent.type = EventType.PRODUCT_LAUNCH_EVENT;
    this.newEvent.ticketPrice = 0;
    this.newEvent.storeId = this.selectedStoreId!;

    this.eventService.create(this.newEvent).subscribe(() => {
      this.showCreateEventModal = false;
      this.loadEvents();
      this.resetEventForm();
    });
  }

  // --- IMAGE UPLOAD LOGIC ---
  onImageSelected(event: any): void {
    const file: File = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;
    this.uploading = true;

    // Upload to backend
    this.uploadService.uploadImage(file).subscribe({
      next: (url) => {
        this.newEvent.imageUrl = url;
        this.imagePreview = url;
        this.uploading = false;
      },
      error: (err) => {
        console.error('Upload failed', err);
        this.uploading = false;
        alert('Image upload failed. Please try again.');
      }
    });
  }

  removeImage(): void {
    this.newEvent.imageUrl = '';
    this.imagePreview = null;
    this.selectedFile = null;
  }

  resetEventForm(): void {
    this.newEvent = {
      title: '', description: '', location: '', imageUrl: '',
      capacity: 100, ticketPrice: 0,
      type: EventType.PRODUCT_LAUNCH_EVENT,
      date: new Date()
    };
    this.imagePreview = null;
    this.selectedFile = null;
  }

  updateEventStatus(id: number, statusStr: string): void {
    const status = statusStr as EventStatus;
    this.eventService.updateStatus(id, status).subscribe(() => this.loadEvents());
  }

  deleteEvent(id: number): void {
    if (confirm('Are you sure you want to delete this event?')) {
      this.eventService.delete(id).subscribe(() => this.loadEvents());
    }
  }
}
