import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../core/services/ticket.service';
import { Ticket } from '../../core/models/ticket.model';

@Component({
    selector: 'app-customer-tickets',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container mt-5">
      <h2>My Tickets</h2>
      
      <div *ngIf="loading" class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>

      <div class="row" *ngIf="!loading">
        <div class="col-md-4 mb-4" *ngFor="let t of tickets">
          <div class="card h-100 shadow-sm border-0 border-start border-primary border-4">
            <div class="card-body text-center">
              <h5 class="card-title fw-bold">{{ t.eventTitle || 'Event Ticket' }}</h5>
              <div class="my-3">
                <!-- Generating an SVG directly for a mockup QR Code -->
                <img [src]="'https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=' + t.qrCode" alt="QR Code" class="img-fluid border p-1 rounded">
              </div>
              <p class="text-muted small">ID: {{ t.qrCode }}</p>
              
              <hr>
              <div class="d-flex justify-content-between">
                <span class="fw-bold">Price:</span>
                <span class="text-success">{{ t.price === 0 ? 'FREE' : (t.price | currency) }}</span>
              </div>
              <div class="d-flex justify-content-between mt-2">
                <span class="fw-bold">Status:</span>
                <span class="badge" [ngClass]="{'bg-success': t.status === 'VALID', 'bg-danger': t.status === 'USED'}">{{ t.status || 'VALID' }}</span>
              </div>
            </div>
            <div class="card-footer bg-white border-0 text-center">
               <button class="btn btn-outline-primary" (click)="showQR(t)">Show QR Code Full</button>
            </div>
          </div>
        </div>
      </div>
      <div *ngIf="!loading && tickets.length === 0" class="alert alert-info mt-4">
        You haven't bought any tickets yet. <a routerLink="/events" class="alert-link">Browse Events</a>
      </div>
    </div>
  `
})
export class CustomerTicketsComponent implements OnInit {
    tickets: Ticket[] = [];
    loading = true;

    constructor(private ticketService: TicketService) { }

    ngOnInit(): void {
        // Mock user id 1
        const userId = 1;
        this.ticketService.getByUser(userId).subscribe({
            next: (data) => {
                this.tickets = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Failed to load tickets', err);
                this.loading = false;
            }
        });
    }

    showQR(ticket: Ticket): void {
        alert("Full QR Code for Scanning:\n" + ticket.qrCode);
    }
}
