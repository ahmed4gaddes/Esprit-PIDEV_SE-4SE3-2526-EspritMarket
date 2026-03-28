import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EventService } from '../../core/services/event.service';
import { Event as MarketEvent } from '../../core/models/event.model';

@Component({
    selector: 'app-events-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './events-list.component.html',
    styleUrls: ['./events-list.component.css']
})
export class EventsListComponent implements OnInit {
    events: MarketEvent[] = [];
    filteredEvents: MarketEvent[] = [];
    loading = true;
    selectedStatus: 'ALL' | string = 'ALL';
    searchTerm = '';
    sortBy: 'upcoming' | 'priceAsc' | 'priceDesc' = 'upcoming';
    readonly statusFilters: Array<'ALL' | string> = ['ALL', 'UPCOMING', 'ONGOING', 'COMPLETED'];

    constructor(private eventService: EventService) { }

    ngOnInit(): void {
        this.eventService.getAll().subscribe({
            next: (data) => {
                this.events = data ?? [];
                this.applyFilters();
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching events', err);
                this.loading = false;
            }
        });
    }

    setStatusFilter(status: 'ALL' | string): void {
        this.selectedStatus = status;
        this.applyFilters();
    }

    onSearchChange(term: string): void {
        this.searchTerm = term;
        this.applyFilters();
    }

    onSearchInput(event: Event): void {
        const value = (event.target as HTMLInputElement | null)?.value ?? '';
        this.onSearchChange(value);
    }

    onSortChange(sortBy: 'upcoming' | 'priceAsc' | 'priceDesc'): void {
        this.sortBy = sortBy;
        this.applyFilters();
    }

    onSortSelectChange(event: Event): void {
        const value = (event.target as HTMLSelectElement | null)?.value as 'upcoming' | 'priceAsc' | 'priceDesc' | undefined;
        if (!value) return;
        this.onSortChange(value);
    }

    getFilterLabel(status: 'ALL' | string): string {
        if (status === 'ALL') {
            return 'All';
        }
        return status.charAt(0) + status.slice(1).toLowerCase();
    }

    get upcomingCount(): number {
        return this.events.filter((event) => event.status === 'UPCOMING').length;
    }

    get freeCount(): number {
        return this.events.filter((event) => (event.ticketPrice || 0) === 0).length;
    }

    private applyFilters(): void {
        const term = this.searchTerm.trim().toLowerCase();

        let filtered = this.events.filter((event) => {
            const matchesStatus = this.selectedStatus === 'ALL' || event.status === this.selectedStatus;
            const matchesTerm = !term
                || (event.title || '').toLowerCase().includes(term)
                || (event.location || '').toLowerCase().includes(term)
                || (event.organizerName || '').toLowerCase().includes(term);
            return matchesStatus && matchesTerm;
        });

        filtered = filtered.sort((a, b) => {
            const aDate = a.date ? new Date(a.date).getTime() : 0;
            const bDate = b.date ? new Date(b.date).getTime() : 0;
            const aPrice = a.ticketPrice || 0;
            const bPrice = b.ticketPrice || 0;

            if (this.sortBy === 'priceAsc') {
                return aPrice - bPrice;
            }
            if (this.sortBy === 'priceDesc') {
                return bPrice - aPrice;
            }
            return aDate - bDate;
        });

        this.filteredEvents = filtered;
    }
}
