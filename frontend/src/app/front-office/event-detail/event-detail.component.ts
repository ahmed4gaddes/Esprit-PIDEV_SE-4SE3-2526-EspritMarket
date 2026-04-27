import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { EventService } from '../../core/services/event.service';
import { TicketService } from '../../core/services/ticket.service';
import { Event } from '../../core/models/event.model';
import { DynamicPriceResponse } from '../../core/models/event.model';
import { LiveSessionService } from '../../core/services/live-session.service';
import { LiveSession } from '../../core/models/live-session.model';

@Component({
    selector: 'app-event-detail',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './event-detail.component.html',
    styleUrls: ['./event-detail.component.css']
})
export class EventDetailComponent implements OnInit {
    event: Event | null = null;
    liveSessions: LiveSession[] = [];
    dynamicPrice: DynamicPriceResponse | null = null;
    loading = true;
    purchasing = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private eventService: EventService,
        private liveSessionService: LiveSessionService,
        private ticketService: TicketService
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.loadEvent(Number(id));
            this.loadLiveSessions(Number(id));
        }
    }

    loadEvent(id: number) {
        this.eventService.getById(id).subscribe({
            next: (data) => {
                this.event = data;
                this.loading = false;
                // Load dynamic price
                this.eventService.getCurrentPrice(id).subscribe({
                    next: (price) => this.dynamicPrice = price,
                    error: () => this.dynamicPrice = null
                });
            },
            error: (err) => {
                console.error('Error fetching event', err);
                this.loading = false;
            }
        });
    }

    loadLiveSessions(eventId: number) {
        this.liveSessionService.getByEvent(eventId).subscribe({
            next: (data) => this.liveSessions = data,
            error: (err) => console.error('Error fetching lives', err)
        });
    }

    buyTicket() {
        if (!this.event || !this.event.id) return;
        this.purchasing = true;
        const userId = 1;

        // Price is now calculated by the backend via DynamicPricingService
        this.ticketService.create(this.event.id, { price: 0, userId }).subscribe({
            next: (ticket) => {
                alert('Ticket purchased successfully! QR Code: ' + ticket.qrCode);
                this.purchasing = false;
                this.loadEvent(this.event!.id!);
            },
            error: (err) => {
                console.error('Purchase failed', err);
                alert('Failed to purchase ticket.');
                this.purchasing = false;
            }
        });
    }

    goBackToEvents(): void {
        if (window.history.length > 1) {
            this.location.back();
            return;
        }
        this.router.navigate(['/events']);
    }
}
