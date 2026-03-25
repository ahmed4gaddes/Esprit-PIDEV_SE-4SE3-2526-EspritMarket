import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../core/services/ticket.service';
import { Ticket } from '../../core/models/ticket.model';
import { AuthService } from '../../auth/auth.service';

@Component({
    selector: 'app-customer-tickets',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './customer-tickets.component.html',
    styleUrls: ['./customer-tickets.component.css']
})
export class CustomerTicketsComponent implements OnInit {
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
        alert("Scannez ce code QR lors de l'événement !\\n\\nID Billet : " + ticket.qrCode);
    }
}
