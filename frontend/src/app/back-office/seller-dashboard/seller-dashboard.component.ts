import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import { LiveSessionService } from '../../core/services/live-session.service';
import { Event, EventType, EventStatus } from '../../core/models/event.model';
import { LiveSession, LivePlatform, LiveSessionStatus } from '../../core/models/live-session.model';

@Component({
    selector: 'app-seller-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="container mt-5 pt-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>📊 Espace Vendeur - Mon Magasin</h2>
          <div>
            <button class="btn btn-outline-primary me-2" (click)="openEventModal()">+ Lancement Produit</button>
            <button class="btn btn-outline-danger" (click)="openLiveModal()">+ Live Promo</button>
          </div>
      </div>

      <div class="row">
        <!-- Events Section -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-calendar-event me-2"></i> Événements liés (Store)</h5>
                    <span class="badge bg-light text-primary rounded-pill">{{ events.length }}</span>
                </div>
                <div class="card-body">
                    <div *ngIf="events.length === 0" class="text-muted text-center py-3">
                        <p>Aucun événement lié à ce magasin pour le moment.</p>
                    </div>
                    <ul class="list-group list-group-flush" *ngIf="events.length > 0">
                        <li class="list-group-item px-0" *ngFor="let event of events">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1 fw-bold">{{ event.title }}</h6>
                                <small class="text-muted">{{ event.date | date:'shortDate' }}</small>
                            </div>
                            <p class="mb-1 text-muted small">{{ event.description || 'Pas de description' }}</p>
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
                    <h5 class="mb-0"><i class="bi bi-camera-video me-2"></i> Sessions Live (Store)</h5>
                    <span class="badge bg-light text-danger rounded-pill">{{ liveSessions.length }}</span>
                </div>
                <div class="card-body">
                    <div *ngIf="liveSessions.length === 0" class="text-muted text-center py-3">
                        <p>Aucune session live planifiée pour ce magasin.</p>
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
                        </li>
                    </ul>
                </div>
            </div>
        </div>
      </div>
      
      <!-- Event Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEventModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Créer un Événement (Magasin actuel)</h5>
              <button type="button" class="btn-close" (click)="showEventModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre de l'événement</label>
                <input class="form-control" [(ngModel)]="newEvent.title" placeholder="Ex: Lancement Nouvelle Gamme">
              </div>
              <div class="mb-3">
                <label>Date et Heure</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
              </div>
              <div class="mb-3">
                <label>Type d'événement</label>
                <select class="form-select" [(ngModel)]="newEvent.type">
                  <option value="PRODUCT_LAUNCH_EVENT">Product Launch Event</option>
                  <option value="STORE_ACTIVE_EVENT">Store Active Event</option>
                  <option value="TOP_SELLER_EVENT">Top Seller Event</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showEventModal = false">Annuler</button>
              <button class="btn btn-primary" (click)="createEvent()">Créer</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Live Session Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showLiveModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Planifier un Live (Magasin actuel)</h5>
              <button type="button" class="btn-close" (click)="showLiveModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre du Live</label>
                <input class="form-control" [(ngModel)]="newLive.title" placeholder="Ex: Présentation Produits en Direct">
              </div>
              <div class="mb-3">
                <label>Plateforme</label>
                <select class="form-select" [(ngModel)]="newLive.platform">
                  <option value="TIKTOK">TikTok</option>
                  <option value="INSTAGRAM">Instagram</option>
                  <option value="YOUTUBE">YouTube</option>
                </select>
              </div>
              <div class="mb-3">
                <label>Date de diffusion</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newLive.scheduledAt">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showLiveModal = false">Annuler</button>
              <button class="btn btn-danger" (click)="createLive()">Planifier</button>
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
        this.liveSessionService.create(this.newLive).subscribe(() => {
            this.showLiveModal = false;
            this.loadStoreData();
        });
    }
}
