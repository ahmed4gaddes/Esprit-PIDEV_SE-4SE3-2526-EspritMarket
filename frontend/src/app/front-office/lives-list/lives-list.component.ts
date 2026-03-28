import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LiveSessionService } from '../../core/services/live-session.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';

@Component({
    selector: 'app-lives-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './lives-list.component.html',
    styleUrls: ['./lives-list.component.css']
})
export class LivesListComponent implements OnInit {
    lives: LiveSession[] = [];
    filteredLives: LiveSession[] = [];
    loading = true;
    selectedStatus: 'ALL' | LiveSessionStatus = 'ALL';
    searchTerm = '';
    sortBy: 'recommended' | 'dateAsc' | 'dateDesc' = 'recommended';
    readonly statusFilters: Array<'ALL' | LiveSessionStatus> = [
        'ALL',
        LiveSessionStatus.LIVE,
        LiveSessionStatus.SCHEDULED,
        LiveSessionStatus.ENDED
    ];

    constructor(private liveSessionService: LiveSessionService) { }

    ngOnInit(): void {
        this.liveSessionService.getAll().subscribe({
            next: (data) => {
                this.lives = data ?? [];
                this.applyFilters();
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching live sessions', err);
                this.loading = false;
            }
        });
    }

    isLive(status?: LiveSessionStatus): boolean {
        return status === LiveSessionStatus.LIVE;
    }

    setStatusFilter(status: 'ALL' | LiveSessionStatus): void {
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

    onSortChange(sortBy: 'recommended' | 'dateAsc' | 'dateDesc'): void {
        this.sortBy = sortBy;
        this.applyFilters();
    }

    onSortSelectChange(event: Event): void {
        const value = (event.target as HTMLSelectElement | null)?.value as 'recommended' | 'dateAsc' | 'dateDesc' | undefined;
        if (!value) return;
        this.onSortChange(value);
    }

    getFilterLabel(status: 'ALL' | LiveSessionStatus): string {
        if (status === 'ALL') {
            return 'All';
        }
        return status.charAt(0) + status.slice(1).toLowerCase();
    }

    get liveCount(): number {
        return this.lives.filter((live) => this.isLive(live.status)).length;
    }

    get scheduledCount(): number {
        return this.lives.filter((live) => live.status === LiveSessionStatus.SCHEDULED).length;
    }

    private applyFilters(): void {
        const term = this.searchTerm.trim().toLowerCase();

        let filtered = this.lives.filter((live) => {
            const matchesStatus = this.selectedStatus === 'ALL' || live.status === this.selectedStatus;
            const matchesTerm = !term
                || (live.title || '').toLowerCase().includes(term)
                || (live.creatorName || '').toLowerCase().includes(term)
                || (live.platform || '').toLowerCase().includes(term);
            return matchesStatus && matchesTerm;
        });

        filtered = filtered.sort((a, b) => {
            const aDate = a.scheduledAt ? new Date(a.scheduledAt).getTime() : 0;
            const bDate = b.scheduledAt ? new Date(b.scheduledAt).getTime() : 0;

            if (this.sortBy === 'dateAsc') {
                return aDate - bDate;
            }
            if (this.sortBy === 'dateDesc') {
                return bDate - aDate;
            }

            // Recommended: LIVE first, then SCHEDULED, then ENDED/CANCELLED.
            const rank = (status?: LiveSessionStatus) => {
                if (status === LiveSessionStatus.LIVE) return 0;
                if (status === LiveSessionStatus.SCHEDULED) return 1;
                return 2;
            };
            const rankDiff = rank(a.status) - rank(b.status);
            return rankDiff !== 0 ? rankDiff : aDate - bDate;
        });

        this.filteredLives = filtered;
    }
}
