import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../core/services/ticket.service';
import { Ticket } from '../../core/models/ticket.model';
import { AuthService } from '../../auth/auth.service';
import { jsPDF } from 'jspdf';

@Component({
    selector: 'app-customer-tickets',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './customer-tickets.component.html',
    styleUrls: ['./customer-tickets.component.css']
})
export class CustomerTicketsComponent implements OnInit, OnChanges {
    @Input() userId: number | null = null;
    @Input() refreshTrigger = 0;

    tickets: Ticket[] = [];
    loading = true;
    currentUserName = 'Participant';

    constructor(
        private ticketService: TicketService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.authService.currentName$.subscribe(name => {
            if (name) this.currentUserName = name;
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['userId'] || changes['refreshTrigger']) {
            this.loadTickets();
        }
    }

    private loadTickets(): void {
        if (!this.userId) {
            this.tickets = [];
            this.loading = false;
            return;
        }
        this.loading = true;
        this.ticketService.getByUser(this.userId).subscribe({
            next: (data) => {
                this.tickets = data || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Failed to load tickets', err);
                this.tickets = [];
                this.loading = false;
            }
        });
    }

    showQR(ticket: Ticket): void {
        alert('Show this QR code at the event entrance.\n\nTicket code: ' + ticket.qrCode);
    }

    exportTicketPdf(ticket: Ticket): void {
        const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
        const title = ticket.eventTitle || 'Event';
        const holder = ticket.userName || this.currentUserName || this.authService.getUserName() || 'Participant';
        const priceLabel = ticket.price === 0 ? 'FREE' : `${ticket.price.toFixed(2)}`;
        const purchase = ticket.purchaseDate
            ? new Date(ticket.purchaseDate).toLocaleString()
            : new Date().toLocaleString();

        doc.setFont('helvetica', 'bold');
        doc.setFontSize(18);
        doc.setTextColor(30, 58, 95);
        doc.text('Event ticket', 105, 24, { align: 'center' });

        doc.setFont('helvetica', 'normal');
        doc.setFontSize(12);
        doc.setTextColor(0, 0, 0);
        doc.text(`Event: ${title}`, 20, 42);
        doc.text(`Holder: ${holder}`, 20, 52);
        doc.text(`Ticket #: ${ticket.id ?? '—'}`, 20, 62);
        doc.text(`Price: ${priceLabel}`, 20, 72);
        doc.text(`Purchase date: ${purchase}`, 20, 82);
        doc.text(`Status: ${String(ticket.status || 'VALID')}`, 20, 92);
        doc.text('Entry code (QR payload):', 20, 106);
        doc.setFont('courier', 'normal');
        doc.setFontSize(10);
        doc.text(ticket.qrCode || '—', 20, 114, { maxWidth: 170 });

        doc.setFont('helvetica', 'italic');
        doc.setFontSize(9);
        doc.setTextColor(90, 90, 90);
        doc.text('Present this PDF at the venue. The QR image is available in My Tickets.', 20, 270, { maxWidth: 170 });

        const safeName = (title || 'ticket').replace(/[^a-z0-9]+/gi, '_').slice(0, 40);
        doc.save(`ticket_${ticket.id ?? 'event'}_${safeName}.pdf`);
    }
}
