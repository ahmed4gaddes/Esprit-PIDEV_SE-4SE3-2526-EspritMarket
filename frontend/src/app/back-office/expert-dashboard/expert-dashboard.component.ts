import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EventService } from '../../core/services/event.service';
import { LiveSessionService } from '../../core/services/live-session.service';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { Event, EventType, EventStatus } from '../../core/models/event.model';
import { LiveSession, LivePlatform, LiveSessionStatus } from '../../core/models/live-session.model';

@Component({
  selector: 'app-expert-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container mt-4 pt-4">
      <h2>🧑‍🏫 Espace Expert</h2>
      
      <!-- Service Selection Block -->
      <div class="card shadow-sm mb-4 border-primary">
        <div class="card-body bg-light">
          <div class="row align-items-center">
            <div class="col-md-6">
              <label class="form-label fw-bold">Sélectionnez le Service à gérer</label>
              <select class="form-select border-primary" [(ngModel)]="selectedServiceId" (change)="onServiceChange()">
                <option [ngValue]="null" disabled selected>-- Choisissez un service --</option>
                <option *ngFor="let s of myServices" [value]="s.id">{{ s.title }}</option>
              </select>
            </div>
            <div class="col-md-6 text-end mt-3 mt-md-0">
              <button class="btn btn-outline-info me-2" [disabled]="!selectedServiceId" (click)="openEventModal()">+ Create Event</button>
              <button class="btn btn-outline-warning text-dark" [disabled]="!selectedServiceId" (click)="openLiveModal()">+ Create Live</button>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="loadingData" class="text-center my-5">
        <div class="spinner-border text-primary" role="status"></div>
      </div>

      <!-- Dashboard Content (hidden if no service selected) -->
      <div *ngIf="selectedServiceId && !loadingData">
        
        <!-- Navigation Tabs -->
        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <a class="nav-link cursor-pointer" [class.active]="activeTab === 'lives'" (click)="activeTab = 'lives'">📺 Live Sessions</a>
          </li>
          <li class="nav-item">
            <a class="nav-link cursor-pointer" [class.active]="activeTab === 'events'" (click)="activeTab = 'events'">📅 Workshops & Certifications</a>
          </li>
          <li class="nav-item">
            <a class="nav-link cursor-pointer" [class.active]="activeTab === 'gamification'" (click)="activeTab = 'gamification'">🏆 Gamification Events</a>
          </li>
        </ul>

        <!-- LIVES TAB -->
        <div *ngIf="activeTab === 'lives'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4>Live Sessions</h4>
            </div>
            <table class="table table-striped table-hover align-middle shadow-sm">
              <thead class="table-dark">
                <tr>
                  <th>Titre</th>
                  <th>Plateforme</th>
                  <th>Date</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let live of liveSessions">
                  <td class="fw-bold">{{ live.title }}</td>
                  <td><span class="badge bg-secondary">{{ live.platform }}</span></td>
                  <td>{{ live.scheduledAt | date:'medium' }}</td>
                  <td>
                    <span class="badge" [ngClass]="{'bg-success': live.status === 'LIVE', 'bg-dark': live.status === 'SCHEDULED', 'bg-warning text-dark': live.status === 'ENDED', 'bg-danger': live.status === 'CANCELLED'}">
                      {{ live.status }}
                    </span>
                  </td>
                  <td>
                    <button *ngIf="live.status === 'SCHEDULED'" class="btn btn-sm btn-success me-2" (click)="updateLiveStatus(live.id!, 'LIVE')">Start</button>
                    <button *ngIf="live.status === 'LIVE'" class="btn btn-sm btn-warning me-2" (click)="updateLiveStatus(live.id!, 'ENDED')">End</button>
                    <a *ngIf="live.status === 'LIVE' && live.platform === 'LOCAL'" class="btn btn-sm btn-info text-white me-2" [routerLink]="'/live/local/' + live.id"><i class="bi bi-camera-video"></i> Rejoindre</a>
                    <button class="btn btn-sm btn-outline-primary me-2" (click)="openEditLive(live)"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-danger" (click)="deleteLive(live.id!)"><i class="bi bi-trash"></i></button>
                  </td>
                </tr>
                <tr *ngIf="liveSessions.length === 0">
                  <td colspan="5" class="text-center text-muted py-3">Aucun live prévu.</td>
                </tr>
              </tbody>
            </table>
        </div>

        <!-- EVENTS TAB -->
        <div *ngIf="activeTab === 'events'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4>Workshops & Certifications</h4>
            </div>
            <table class="table table-striped table-hover align-middle shadow-sm">
              <thead class="table-dark">
                <tr>
                  <th>Titre</th>
                  <th>Type</th>
                  <th>Date</th>
                  <th>Capacité</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let event of filteredEvents('standard')">
                  <td class="fw-bold">{{ event.title }}</td>
                  <td><span class="badge bg-primary">{{ event.type }}</span></td>
                  <td>{{ event.date | date:'shortDate' }}</td>
                  <td>{{ event.ticketCount || 0 }} / {{ event.capacity }}</td>
                  <td><span class="badge border bg-white text-dark">{{ event.status || 'UPCOMING' }}</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary me-2" (click)="openEditEvent(event)"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-danger" (click)="deleteEvent(event.id!)"><i class="bi bi-trash"></i></button>
                  </td>
                </tr>
                <tr *ngIf="filteredEvents('standard').length === 0">
                  <td colspan="6" class="text-center text-muted py-3">Aucun événement prévu.</td>
                </tr>
              </tbody>
            </table>
        </div>

        <!-- GAMIFICATION TAB -->
        <div *ngIf="activeTab === 'gamification'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4>Gamification Events (Tickets)</h4>
              <button class="btn btn-primary btn-sm" (click)="openGamificationModal()">+ Ajouter Gamification Event</button>
            </div>
            <table class="table table-striped table-hover align-middle shadow-sm">
              <thead class="table-dark">
                <tr>
                  <th>Titre</th>
                  <th>Date</th>
                  <th>Prix Ticket</th>
                  <th>Capacité</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let event of filteredEvents('gamification')">
                  <td class="fw-bold">{{ event.title }} <i class="bi bi-trophy-fill text-warning ms-2"></i></td>
                  <td>{{ event.date | date:'shortDate' }}</td>
                  <td class="text-success fw-bold">{{ event.ticketPrice | currency }}</td>
                  <td>{{ event.ticketCount || 0 }} / {{ event.capacity }}</td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary me-2" (click)="openEditEvent(event)"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-danger" (click)="deleteEvent(event.id!)"><i class="bi bi-trash"></i></button>
                  </td>
                </tr>
                <tr *ngIf="filteredEvents('gamification').length === 0">
                  <td colspan="5" class="text-center text-muted py-3">Aucune compétition prévue.</td>
                </tr>
              </tbody>
            </table>
        </div>
      </div>

      <!-- Modals -->

      <!-- Event Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEventModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Créer un Événement Standard</h5>
              <button type="button" class="btn-close" (click)="showEventModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre</label>
                <input class="form-control" [(ngModel)]="newEvent.title" placeholder="Titre de l'événement">
              </div>
              <div class="mb-3">
                <label>Description</label>
                <textarea class="form-control" [(ngModel)]="newEvent.description"></textarea>
              </div>
              <div class="mb-3">
                <label>Type</label>
                <select class="form-select" [(ngModel)]="newEvent.type">
                  <option value="WORKSHOP_EVENT">Workshop</option>
                  <option value="CERTIFICATION_EVENT">Certification</option>
                  <option value="NETWORKING_EVENT">Networking</option>
                </select>
              </div>
              <div class="mb-3">
                <label>Date et Heure</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
              </div>
              <div class="mb-3">
                <label>Capacité Max</label>
                <input type="number" class="form-control" [(ngModel)]="newEvent.capacity">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showEventModal = false">Annuler</button>
              <button class="btn btn-primary" (click)="createEvent()">Créer</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Gamification Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showGamificationModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content border-warning">
            <div class="modal-header bg-warning">
              <h5 class="modal-title fw-bold">🏆 Créer un Événement Gamification</h5>
              <button type="button" class="btn-close" (click)="showGamificationModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre de la compétition</label>
                <input class="form-control" [(ngModel)]="newEvent.title" placeholder="Ex: Hackathon Expert">
              </div>
              <div class="mb-3">
                <label>Description des récompenses</label>
                <textarea class="form-control" [(ngModel)]="newEvent.description"></textarea>
              </div>
              <div class="mb-3">
                <label>Date</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newEvent.date">
              </div>
              <div class="row">
                <div class="col-6 mb-3">
                  <label>Capacité Max</label>
                  <input type="number" class="form-control" [(ngModel)]="newEvent.capacity">
                </div>
                <div class="col-6 mb-3">
                  <label>Prix du Ticket ($)</label>
                  <input type="number" class="form-control" [(ngModel)]="newEvent.ticketPrice">
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showGamificationModal = false">Annuler</button>
              <button class="btn btn-warning fw-bold" (click)="createEvent(true)">Publier Compétition</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Live Session Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showLiveModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Créer un Live Session</h5>
              <button type="button" class="btn-close" (click)="showLiveModal = false"></button>
            </div>
            <div class="modal-body text-start">
              <div class="mb-3">
                <label>Subject / Title *</label>
                <input type="text" class="form-control" [(ngModel)]="newLive.title" placeholder="Ex: Masterclass Java">
              </div>
              <div class="mb-3">
                <label>Platform</label>
                <select class="form-select" [(ngModel)]="newLive.platform">
                  <option value="ZOOM">Zoom</option>
                  <option value="GOOGLE_MEET">Google Meet</option>
                  <option value="YOUTUBE">YouTube</option>
                  <option value="LOCAL">LOCAL (On-site Chat & Video)</option>
                </select>
              </div>
              <div *ngIf="newLive.platform !== 'LOCAL'" class="mb-3">
                <label>External Link</label>
                <input type="text" class="form-control" [(ngModel)]="newLive.link" placeholder="https://zoom.us/j/...">
              </div>
              <div class="mb-3">
                <label>Date et Heure *</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="newLive.scheduledAt">
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="showLiveModal = false">Cancel</button>
              <button type="button" class="btn btn-primary" (click)="createLive()">Planifier Live</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Edit Live Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEditLiveModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Modifier Live Session</h5>
              <button type="button" class="btn-close" (click)="showEditLiveModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre</label>
                <input type="text" class="form-control" [(ngModel)]="editLivePayload.title">
              </div>
              <div class="mb-3">
                <label>Plateforme</label>
                <select class="form-select" [(ngModel)]="editLivePayload.platform">
                  <option value="ZOOM">Zoom</option>
                  <option value="GOOGLE_MEET">Google Meet</option>
                  <option value="YOUTUBE">YouTube</option>
                  <option value="LOCAL">LOCAL</option>
                </select>
              </div>
              <div *ngIf="editLivePayload.platform !== 'LOCAL'" class="mb-3">
                <label>Lien externe</label>
                <input type="text" class="form-control" [(ngModel)]="editLivePayload.link">
              </div>
              <div class="mb-3">
                <label>Date et Heure</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="editLivePayload.scheduledAt">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showEditLiveModal = false">Annuler</button>
              <button class="btn btn-primary" (click)="updateLive()">Enregistrer</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Edit Event Modal -->
      <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEditEventModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Modifier Événement</h5>
              <button type="button" class="btn-close" (click)="showEditEventModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label>Titre</label>
                <input type="text" class="form-control" [(ngModel)]="editEventPayload.title">
              </div>
              <div class="mb-3">
                <label>Type</label>
                <select class="form-select" [(ngModel)]="editEventPayload.type">
                  <option value="WORKSHOP_EVENT">Workshop</option>
                  <option value="CERTIFICATION_EVENT">Certification</option>
                  <option value="NETWORKING_EVENT">Networking</option>
                  <option value="GAMIFICATION_EVENT">Gamification</option>
                </select>
              </div>
              <div class="mb-3">
                <label>Lieu / Lien</label>
                <input type="text" class="form-control" [(ngModel)]="editEventPayload.location">
              </div>
              <div class="mb-3">
                <label>Capacité</label>
                <input type="number" class="form-control" [(ngModel)]="editEventPayload.capacity">
              </div>
              <div class="mb-3">
                <label>Prix Ticket ($)</label>
                <input type="number" class="form-control" [(ngModel)]="editEventPayload.ticketPrice">
              </div>
              <div class="mb-3">
                <label>Date</label>
                <input type="datetime-local" class="form-control" [(ngModel)]="editEventPayload.date">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="showEditEventModal = false">Annuler</button>
              <button class="btn btn-primary" (click)="updateEvent()">Enregistrer</button>
            </div>
          </div>
        </div>
      </div>

    </div>
  `,
  styles: []
})
export class ExpertDashboardComponent implements OnInit {
  activeTab: 'lives' | 'events' | 'gamification' = 'lives';
  
  myServices: any[] = [];
  selectedServiceId: number | null = null;
  loadingData = false;

  events: Event[] = [];
  liveSessions: LiveSession[] = [];
  
  showEventModal = false;
  showGamificationModal = false;
  showLiveModal = false;
  showEditLiveModal = false;
  showEditEventModal = false;
  
  newEvent: Partial<Event> = {};
  newLive: Partial<LiveSession> = {};

  editLiveId: number | null = null;
  editLivePayload: Partial<LiveSession> = {};
  editEventId: number | null = null;
  editEventPayload: Partial<Event> = {};

  constructor(
      private serviceModuleService: ServiceModuleService,
      private eventService: EventService,
      private liveSessionService: LiveSessionService
  ) {}

  ngOnInit(): void {
      this.loadMyServices();
  }

  loadMyServices(): void {
      this.serviceModuleService.getMyServices().subscribe({
          next: (services) => {
              this.myServices = services;
          },
          error: (err) => console.error("Error loading services", err)
      });
  }

  onServiceChange() {
      if (this.selectedServiceId) {
          this.loadServiceData();
      }
  }

  loadServiceData(): void {
      this.loadingData = true;
      this.eventService.getByService(this.selectedServiceId!).subscribe(data => {
          this.events = data;
          this.liveSessionService.getByService(this.selectedServiceId!).subscribe(lives => {
              this.liveSessions = lives;
              this.loadingData = false;
          });
      });
  }

  filteredEvents(category: 'standard' | 'gamification'): Event[] {
      if (category === 'gamification') {
          return this.events.filter(e => e.type === EventType.GAMIFICATION_EVENT);
      } else {
          return this.events.filter(e => e.type !== EventType.GAMIFICATION_EVENT);
      }
  }
  
  resetForms(): void {
      this.newEvent = { title: '', description: '', location: 'Online', capacity: 20, ticketPrice: 0, date: new Date() };
      this.newLive = { title: '', link: '', platform: LivePlatform.ZOOM, scheduledAt: new Date() };
  }

  openEventModal() {
      this.resetForms();
      this.newEvent.type = EventType.WORKSHOP_EVENT;
      this.showEventModal = true;
  }

  openGamificationModal() {
      this.resetForms();
      this.newEvent.type = EventType.GAMIFICATION_EVENT as EventType;
      this.showGamificationModal = true;
  }
  
  openLiveModal() {
      this.resetForms();
      this.showLiveModal = true;
  }
  
  createEvent(isGamification = false) {
      const payload = { ...this.newEvent };
      
      // Convert dates
      if (payload.date && typeof payload.date === 'string') {
          payload.date = new Date(payload.date);
      }
      
      payload.serviceId = this.selectedServiceId!;
      
      this.eventService.create(payload).subscribe({
          next: () => {
              this.showEventModal = false;
              this.showGamificationModal = false;
              this.loadServiceData();
          },
          error: (err) => alert("Erreur: " + err.message)
      });
  }
  
  createLive() {
      let livePayload = { ...this.newLive };
      
      // Fix datetime extraction issue
      if (livePayload.scheduledAt && typeof livePayload.scheduledAt === 'string') {
          livePayload.scheduledAt = new Date(livePayload.scheduledAt);
      }

      livePayload.serviceId = this.selectedServiceId!;
      
      this.liveSessionService.create(livePayload).subscribe({
          next: () => {
              this.showLiveModal = false;
              this.loadServiceData();
          },
          error: (err) => {
              console.error(err);
              alert("Erreur lors de la création du live.");
          }
      });
  }

  updateLiveStatus(id: number, statusStr: string): void {
      const status = statusStr as LiveSessionStatus;
      this.liveSessionService.updateStatus(id, status).subscribe(() => this.loadServiceData());
  }

  deleteLive(id: number): void {
      if (confirm("Supprimer ce live ?")) {
          this.liveSessionService.delete(id).subscribe(() => this.loadServiceData());
      }
  }

  deleteEvent(id: number): void {
      if (confirm("Supprimer cet événement ?")) {
          this.eventService.delete(id).subscribe(() => this.loadServiceData());
      }
  }

  // ── Edit Live Session ──
  openEditLive(live: LiveSession) {
      this.editLiveId = live.id!;
      let initDate = live.scheduledAt;
      if (initDate) {
          const d = new Date(initDate);
          d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
          initDate = d.toISOString().slice(0, 16) as any;
      }
      this.editLivePayload = { ...live, scheduledAt: initDate as any };
      this.showEditLiveModal = true;
  }

  updateLive() {
      if (!this.editLiveId) return;
      let payload = { ...this.editLivePayload };
      if (payload.scheduledAt && typeof payload.scheduledAt === 'string') {
          payload.scheduledAt = new Date(payload.scheduledAt);
      }
      this.liveSessionService.update(this.editLiveId, payload as any).subscribe({
          next: () => {
              this.showEditLiveModal = false;
              this.loadServiceData();
          },
          error: (err) => alert('Erreur: ' + (err.error?.message || err.message))
      });
  }

  // ── Edit Event ──
  openEditEvent(event: Event) {
      this.editEventId = event.id!;
      let initDate = event.date;
      if (initDate) {
          const d = new Date(initDate);
          d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
          initDate = d.toISOString().slice(0, 16) as any;
      }
      this.editEventPayload = { ...event, date: initDate as any };
      this.showEditEventModal = true;
  }

  updateEvent() {
      if (!this.editEventId) return;
      let payload = { ...this.editEventPayload };
      if (payload.date && typeof payload.date === 'string') {
          payload.date = new Date(payload.date);
      }
      this.eventService.update(this.editEventId, payload as any).subscribe({
          next: () => {
              this.showEditEventModal = false;
              this.loadServiceData();
          },
          error: (err) => alert('Erreur: ' + (err.error?.message || err.message))
      });
  }
}

