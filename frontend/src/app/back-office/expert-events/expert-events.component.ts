import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import { HttpClient } from '@angular/common/http';
import { Event, EventType, EventStatus, PricingRule } from '../../core/models/event.model';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-expert-events',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="container mt-4">
      <h2>Expert Workshops & Certifications</h2>
      <button class="btn btn-primary mb-3" (click)="showCreateModal = true">Create New Event</button>

      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>Title</th>
            <th>Type</th>
            <th>Date</th>
            <th>Capacity</th>
            <th>Price</th>
            <th>Pricing</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let event of events">
            <td>{{ event.title }}</td>
            <td><span class="badge bg-secondary">{{ event.type }}</span></td>
            <td>{{ event.date | date:'shortDate' }}</td>
            <td>{{ event.ticketCount || 0 }} / {{ event.capacity }}</td>
            <td>
              <span *ngIf="event.dynamicPricingEnabled" class="text-success fw-bold">
                {{ event.currentDynamicPrice | currency }} <small class="text-muted">(dyn.)</small>
              </span>
              <span *ngIf="!event.dynamicPricingEnabled">
                {{ event.ticketPrice | currency }}
              </span>
            </td>
            <td>
              <span *ngIf="event.dynamicPricingEnabled" class="badge bg-success">⚡ Dynamic</span>
              <span *ngIf="!event.dynamicPricingEnabled" class="badge bg-light text-dark border">Fixed</span>
            </td>
            <td><span class="badge" [ngClass]="{'bg-success': event.status === 'UPCOMING', 'bg-warning': event.status === 'ONGOING'}">{{ event.status }}</span></td>
            <td>
              <button class="btn btn-sm me-1" [ngClass]="event.dynamicPricingEnabled ? 'btn-outline-success' : 'btn-success'" (click)="openPricingModal(event)" title="Dynamic Pricing">⚡</button>
              <button *ngIf="event.status === 'UPCOMING'" class="btn btn-sm btn-warning me-1" (click)="updateStatus(event.id!, 'ONGOING')">Start</button>
              <button *ngIf="event.status === 'ONGOING'" class="btn btn-sm btn-secondary me-1" (click)="updateStatus(event.id!, 'COMPLETED')">Complete</button>
              <button class="btn btn-sm btn-info me-1" (click)="openEditEvent(event)"><i class="bi bi-pencil"></i></button>
              <button class="btn btn-sm btn-danger" (click)="deleteEvent(event.id!)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create Modal -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showCreateModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Expert Event</h5>
            <button type="button" class="btn-close" (click)="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label>Title</label>
              <input type="text" class="form-control" [(ngModel)]="newEvent.title">
            </div>
            <div class="mb-3">
              <label>Type</label>
              <select class="form-select" [(ngModel)]="newEvent.type">
                <option value="WORKSHOP_EVENT">Workshop</option>
                <option value="CERTIFICATION_EVENT">Certification</option>
              </select>
            </div>
            <div class="mb-3">
              <label>Location / Link</label>
              <input type="text" class="form-control" [(ngModel)]="newEvent.location">
            </div>
            <div class="mb-3">
              <label>Event Image</label>
              <input type="file" class="form-control" (change)="onFileSelected($event, 'create')" accept="image/*">
              <div *ngIf="newEvent.imageUrl" class="mt-2 text-center">
                <img [src]="newEvent.imageUrl" alt="Preview" class="img-thumbnail" style="max-height: 150px;">
              </div>
              <div *ngIf="uploadingCreate" class="text-primary small mt-1">Uploading...</div>
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

    <!-- Edit Modal -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showEditModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Expert Event</h5>
            <button type="button" class="btn-close" (click)="showEditModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label>Title</label>
              <input type="text" class="form-control" [(ngModel)]="editEventPayload.title">
            </div>
            <div class="mb-3">
              <label>Type</label>
              <select class="form-select" [(ngModel)]="editEventPayload.type">
                <option value="WORKSHOP_EVENT">Workshop</option>
                <option value="CERTIFICATION_EVENT">Certification</option>
              </select>
            </div>
            <div class="mb-3">
              <label>Location / Link</label>
              <input type="text" class="form-control" [(ngModel)]="editEventPayload.location">
            </div>
            <div class="mb-3">
              <label>Event Image</label>
              <input type="file" class="form-control" (change)="onFileSelected($event, 'edit')" accept="image/*">
              <div *ngIf="editEventPayload.imageUrl" class="mt-2 text-center">
                <img [src]="editEventPayload.imageUrl" alt="Preview" class="img-thumbnail" style="max-height: 150px;">
              </div>
              <div *ngIf="uploadingEdit" class="text-primary small mt-1">Uploading...</div>
            </div>
            <div class="mb-3">
              <label>Capacity</label>
              <input type="number" class="form-control" [(ngModel)]="editEventPayload.capacity">
            </div>
            <div class="mb-3">
              <label>Ticket Price ($)</label>
              <input type="number" class="form-control" [(ngModel)]="editEventPayload.ticketPrice">
            </div>
            <div class="mb-3">
              <label>Date</label>
              <input type="datetime-local" class="form-control" [(ngModel)]="editEventPayload.date">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showEditModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" (click)="updateEvent()">Save Changes</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ⚡ Dynamic Pricing Modal -->
    <div class="modal d-block bg-dark bg-opacity-50" *ngIf="showPricingModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
            <h5 class="modal-title">⚡ Dynamic Pricing — {{ pricingEventTitle }}</h5>
            <button type="button" class="btn-close btn-close-white" (click)="showPricingModal = false"></button>
          </div>
          <div class="modal-body">

            <!-- Toggle -->
            <div class="form-check form-switch mb-4">
              <input class="form-check-input" type="checkbox" [(ngModel)]="pricingConfig.dynamicPricingEnabled" id="dpToggle" style="width: 3em; height: 1.5em;">
              <label class="form-check-label fw-bold fs-5 ms-2" for="dpToggle">
                {{ pricingConfig.dynamicPricingEnabled ? '✅ Dynamic Pricing Activé' : '❌ Dynamic Pricing Désactivé' }}
              </label>
            </div>

            <div *ngIf="pricingConfig.dynamicPricingEnabled">
              <!-- Early Bird -->
              <div class="card mb-3 border-0 shadow-sm">
                <div class="card-body">
                  <h6 class="fw-bold">🐦 Phase Early Bird <small class="text-muted">(Remplissage faible → réduction)</small></h6>
                  <div class="row g-3 mt-1">
                    <div class="col-md-6">
                      <label class="form-label">Réduction (%)</label>
                      <div class="input-group">
                        <span class="input-group-text">-</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.earlyBirdDiscount * 100" (ngModelChange)="pricingConfig.earlyBirdDiscount = $event / 100" min="0" max="99" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">Jusqu'à (% remplissage)</label>
                      <div class="input-group">
                        <span class="input-group-text">&lt;</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.earlyBirdThreshold * 100" (ngModelChange)="pricingConfig.earlyBirdThreshold = $event / 100" min="1" max="99" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- High Demand -->
              <div class="card mb-3 border-0 shadow-sm">
                <div class="card-body">
                  <h6 class="fw-bold">📈 Phase Forte Demande <small class="text-muted">(Remplissage moyen → surcharge)</small></h6>
                  <div class="row g-3 mt-1">
                    <div class="col-md-6">
                      <label class="form-label">Surcharge (%)</label>
                      <div class="input-group">
                        <span class="input-group-text">+</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.highDemandSurcharge * 100" (ngModelChange)="pricingConfig.highDemandSurcharge = $event / 100" min="0" max="200" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">À partir de (% remplissage)</label>
                      <div class="input-group">
                        <span class="input-group-text">≥</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.highDemandThreshold * 100" (ngModelChange)="pricingConfig.highDemandThreshold = $event / 100" min="1" max="99" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Last Seats -->
              <div class="card mb-3 border-0 shadow-sm">
                <div class="card-body">
                  <h6 class="fw-bold">🔥 Phase Dernières Places <small class="text-muted">(Quasi-complet → grosse surcharge)</small></h6>
                  <div class="row g-3 mt-1">
                    <div class="col-md-6">
                      <label class="form-label">Surcharge (%)</label>
                      <div class="input-group">
                        <span class="input-group-text">+</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.lastSeatsSurcharge * 100" (ngModelChange)="pricingConfig.lastSeatsSurcharge = $event / 100" min="0" max="300" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">À partir de (% remplissage)</label>
                      <div class="input-group">
                        <span class="input-group-text">≥</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.lastSeatsThreshold * 100" (ngModelChange)="pricingConfig.lastSeatsThreshold = $event / 100" min="1" max="99" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Last Minute -->
              <div class="card mb-3 border-0 shadow-sm">
                <div class="card-body">
                  <h6 class="fw-bold">⏰ Phase Last Minute <small class="text-muted">(Temps restant faible → surcharge cumulative)</small></h6>
                  <div class="row g-3 mt-1">
                    <div class="col-md-6">
                      <label class="form-label">Surcharge (%)</label>
                      <div class="input-group">
                        <span class="input-group-text">+</span>
                        <input type="number" class="form-control" [ngModel]="pricingConfig.lastMinuteSurcharge * 100" (ngModelChange)="pricingConfig.lastMinuteSurcharge = $event / 100" min="0" max="200" step="1">
                        <span class="input-group-text">%</span>
                      </div>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">Si moins de (heures avant)</label>
                      <div class="input-group">
                        <span class="input-group-text">&lt;</span>
                        <input type="number" class="form-control" [(ngModel)]="pricingConfig.lastMinuteHours" min="1" max="168" step="1">
                        <span class="input-group-text">h</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Preview -->
              <div class="alert alert-info" *ngIf="pricingBasePrice > 0">
                <strong>📊 Aperçu pour un prix de base de {{ pricingBasePrice | currency }} :</strong>
                <ul class="mb-0 mt-2">
                  <li>🐦 Early Bird : <strong>{{ pricingBasePrice * (1 - pricingConfig.earlyBirdDiscount) | currency }}</strong> (jusqu'à {{ pricingConfig.earlyBirdThreshold * 100 }}% rempli)</li>
                  <li>💡 Normal : <strong>{{ pricingBasePrice | currency }}</strong> ({{ pricingConfig.earlyBirdThreshold * 100 }}% — {{ pricingConfig.highDemandThreshold * 100 }}%)</li>
                  <li>📈 Forte Demande : <strong>{{ pricingBasePrice * (1 + pricingConfig.highDemandSurcharge) | currency }}</strong> ({{ pricingConfig.highDemandThreshold * 100 }}% — {{ pricingConfig.lastSeatsThreshold * 100 }}%)</li>
                  <li>🔥 Dernières Places : <strong>{{ pricingBasePrice * (1 + pricingConfig.lastSeatsSurcharge) | currency }}</strong> (> {{ pricingConfig.lastSeatsThreshold * 100 }}%)</li>
                </ul>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button *ngIf="pricingRuleExists" type="button" class="btn btn-outline-danger me-auto" (click)="deletePricingRule()">
              🗑️ Supprimer le Dynamic Pricing
            </button>
            <button type="button" class="btn btn-secondary" (click)="showPricingModal = false">Annuler</button>
            <button type="button" class="btn text-white" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" (click)="savePricingRule()">
              {{ pricingRuleExists ? '💾 Mettre à jour' : '⚡ Activer' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ExpertEventsComponent implements OnInit {
    events: Event[] = [];
    showCreateModal = false;

    newEvent: Partial<Event> = {
        title: '',
        location: '',
        imageUrl: '',
        type: EventType.WORKSHOP_EVENT,
        capacity: 20,
        ticketPrice: 0,
        date: new Date()
    };

    showEditModal = false;
    editEventId: number | null = null;
    editEventPayload: Partial<Event> = {};

    uploadingCreate = false;
    uploadingEdit = false;

    // Dynamic Pricing
    showPricingModal = false;
    pricingEventId: number | null = null;
    pricingEventTitle = '';
    pricingBasePrice = 0;
    pricingRuleExists = false;
    pricingConfig: PricingRule = {
        dynamicPricingEnabled: true,
        earlyBirdDiscount: 0.20,
        earlyBirdThreshold: 0.30,
        highDemandSurcharge: 0.20,
        highDemandThreshold: 0.60,
        lastSeatsSurcharge: 0.50,
        lastSeatsThreshold: 0.85,
        lastMinuteSurcharge: 0.30,
        lastMinuteHours: 24
    };

    constructor(private eventService: EventService, private http: HttpClient) { }

    ngOnInit(): void {
        this.loadEvents();
    }

    loadEvents(): void {
        this.eventService.getAll().subscribe(data => this.events = data.filter(e =>
            e.type === EventType.WORKSHOP_EVENT || e.type === EventType.CERTIFICATION_EVENT
        ));
    }

    createEvent(): void {
        this.eventService.create(this.newEvent).subscribe(() => {
            this.showCreateModal = false;
            this.loadEvents();
            this.newEvent = { title: '', location: '', imageUrl: '', type: EventType.WORKSHOP_EVENT, capacity: 20, ticketPrice: 0, date: new Date() };
        });
    }

    onFileSelected(event: any, mode: 'create' | 'edit') {
        const file: File = event.target.files[0];
        if (file) {
            if (mode === 'create') this.uploadingCreate = true;
            else this.uploadingEdit = true;

            const formData = new FormData();
            formData.append('file', file);

            this.http.post<{ url: string, filename: string }>(`${environment.apiUrl}/api/upload`, formData).subscribe({
                next: (res) => {
                    if (mode === 'create') {
                        this.newEvent.imageUrl = res.url;
                        this.uploadingCreate = false;
                    } else {
                        this.editEventPayload.imageUrl = res.url;
                        this.uploadingEdit = false;
                    }
                },
                error: (err) => {
                    console.error('Upload failed', err);
                    if (mode === 'create') this.uploadingCreate = false;
                    else this.uploadingEdit = false;
                    alert('Erreur lors de l\'upload de l\'image.');
                }
            });
        }
    }

    updateStatus(id: number, statusStr: string): void {
        const status = statusStr as EventStatus;
        this.eventService.updateStatus(id, status).subscribe(() => this.loadEvents());
    }

    openEditEvent(event: Event) {
        this.editEventId = event.id!;
        let initDate = event.date;
        if (initDate) {
            const d = new Date(initDate);
            d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
            initDate = d.toISOString().slice(0, 16) as any;
        }
        this.editEventPayload = { ...event, date: initDate as any };
        this.showEditModal = true;
    }

    updateEvent() {
        if (!this.editEventId) return;
        let payload = { ...this.editEventPayload };
        if (payload.date && typeof payload.date === 'string') {
            payload.date = new Date(payload.date);
        }
        this.eventService.update(this.editEventId, payload as any).subscribe({
            next: () => {
                this.showEditModal = false;
                this.loadEvents();
            },
            error: (err) => alert('Erreur: ' + (err.error?.message || err.message))
        });
    }

    deleteEvent(id: number): void {
        if (confirm('Delete this event?')) {
            this.eventService.delete(id).subscribe(() => this.loadEvents());
        }
    }

    // =====================================================================
    // DYNAMIC PRICING
    // =====================================================================
    openPricingModal(event: Event): void {
        this.pricingEventId = event.id!;
        this.pricingEventTitle = event.title;
        this.pricingBasePrice = event.ticketPrice;

        // Try to load existing rule
        this.eventService.getPricingRule(event.id!).subscribe({
            next: (rule) => {
                this.pricingConfig = { ...rule };
                this.pricingRuleExists = true;
                this.showPricingModal = true;
            },
            error: () => {
                // No rule exists, use defaults
                this.pricingConfig = {
                    dynamicPricingEnabled: true,
                    earlyBirdDiscount: 0.20,
                    earlyBirdThreshold: 0.30,
                    highDemandSurcharge: 0.20,
                    highDemandThreshold: 0.60,
                    lastSeatsSurcharge: 0.50,
                    lastSeatsThreshold: 0.85,
                    lastMinuteSurcharge: 0.30,
                    lastMinuteHours: 24
                };
                this.pricingRuleExists = false;
                this.showPricingModal = true;
            }
        });
    }

    savePricingRule(): void {
        if (!this.pricingEventId) return;

        const obs = this.pricingRuleExists
            ? this.eventService.updatePricingRule(this.pricingEventId, this.pricingConfig)
            : this.eventService.createPricingRule(this.pricingEventId, this.pricingConfig);

        obs.subscribe({
            next: () => {
                this.showPricingModal = false;
                this.loadEvents();
                alert('⚡ Dynamic Pricing ' + (this.pricingRuleExists ? 'mis à jour' : 'activé') + ' avec succès !');
            },
            error: (err) => alert('Erreur: ' + (err.error?.message || err.message))
        });
    }

    deletePricingRule(): void {
        if (!this.pricingEventId) return;
        if (!confirm('Supprimer le Dynamic Pricing ? Le prix reviendra au tarif fixe.')) return;

        this.eventService.deletePricingRule(this.pricingEventId).subscribe({
            next: () => {
                this.showPricingModal = false;
                this.loadEvents();
                alert('Dynamic Pricing supprimé. Retour au prix fixe.');
            },
            error: (err) => alert('Erreur: ' + (err.error?.message || err.message))
        });
    }
}
