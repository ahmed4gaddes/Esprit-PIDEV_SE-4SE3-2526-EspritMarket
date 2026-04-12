import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../core/services/event.service';
import { EventStatistics } from '../../core/models/event.model';

@Component({
    selector: 'app-event-statistics',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="dashboard-container container py-4">
        <!-- Dashboard Header -->
        <div class="row mb-5">
            <div class="col-12">
                <div class="dashboard-header-card p-4 rounded-4 shadow-sm position-relative overflow-hidden">
                    <div class="decoration-circle-1"></div>
                    <div class="decoration-circle-2"></div>
                    <div class="position-relative z-1">
                        <h1 class="dashboard-title">
                            <i class="fas fa-chart-pie me-2"></i>
                            Tableau de Bord — Statistiques Événements
                        </h1>
                        <p class="dashboard-subtitle">Vue d'ensemble de vos événements, tickets vendus et revenus générés</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- KPI Cards -->
        <div class="row g-4 mb-5" *ngIf="!loading">
            <!-- Total Events Card -->
            <div class="col-md-4">
                <div class="kpi-card stats-events h-100 p-4 rounded-4 shadow-sm">
                    <div class="kpi-icon-wrapper mb-3">
                        <i class="fas fa-calendar-check text-primary"></i>
                    </div>
                    <h3 class="kpi-value mb-1">{{ statistics.length }}</h3>
                    <p class="kpi-label text-muted mb-0">Total Événements</p>
                </div>
            </div>

            <!-- Total Tickets Sold Card -->
            <div class="col-md-4">
                <div class="kpi-card stats-tickets h-100 p-4 rounded-4 shadow-sm">
                    <div class="kpi-icon-wrapper mb-3">
                        <i class="fas fa-ticket-alt text-success"></i>
                    </div>
                    <h3 class="kpi-value mb-1">{{ totalTickets }}</h3>
                    <p class="kpi-label text-muted mb-0">Tickets Vendus</p>
                </div>
            </div>

            <!-- Total Revenue Card -->
            <div class="col-md-4">
                <div class="kpi-card stats-revenue h-100 p-4 rounded-4 shadow-sm">
                    <div class="kpi-icon-wrapper mb-3">
                        <i class="fas fa-coins text-warning"></i>
                    </div>
                    <h3 class="kpi-value mb-1">{{ totalRevenue | currency:'TND':'symbol':'1.2-2' }}</h3>
                    <p class="kpi-label text-muted mb-0">Revenu Total</p>
                </div>
            </div>
        </div>

        <!-- Statistics Table -->
        <div class="row" *ngIf="!loading">
            <div class="col-12">
                <div class="card border-0 shadow-sm rounded-4 table-card overflow-hidden">
                    <div class="card-header bg-white border-bottom-0 py-4 px-4 d-flex justify-content-between align-items-center">
                        <h5 class="card-title fw-bold m-0 text-dark">
                            <i class="fas fa-list-ul me-2 text-primary"></i>Détails par Événement
                        </h5>
                        <span class="badge bg-primary-subtle text-primary rounded-pill px-3 py-2">Mise à jour en direct</span>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle custom-table mb-0">
                                <thead class="table-light text-muted">
                                    <tr>
                                        <th class="ps-4 fw-semibold text-uppercase font-size-sm">Nom de l'Événement</th>
                                        <th class="fw-semibold text-uppercase font-size-sm text-center">Date</th>
                                        <th class="fw-semibold text-uppercase font-size-sm text-center">Vendus</th>
                                        <th class="pe-4 fw-semibold text-uppercase font-size-sm text-end">Revenu (TND)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr *ngFor="let stat of statistics" class="transition-all">
                                        <td class="ps-4 py-3">
                                            <div class="d-flex align-items-center">
                                                <div class="icon-shape bg-light-primary text-primary rounded-circle me-3 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                                    <i class="fas fa-calendar-day"></i>
                                                </div>
                                                <div>
                                                    <h6 class="mb-0 fw-bold text-dark">{{ stat.title }}</h6>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="text-center py-3">
                                            <span class="text-secondary fw-medium">{{ stat.date | date:'mediumDate' }}</span>
                                        </td>
                                        <td class="text-center py-3">
                                            <span class="badge" [ngClass]="stat.ticketsSold > 0 ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'">
                                                {{ stat.ticketsSold }} ticket(s)
                                            </span>
                                        </td>
                                        <td class="pe-4 text-end py-3">
                                            <span class="fw-bold" [ngClass]="stat.totalRevenue > 0 ? 'text-dark' : 'text-muted'">
                                                {{ stat.totalRevenue | currency:'TND':'symbol':'1.2-2' }}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr *ngIf="statistics.length === 0">
                                        <td colspan="4" class="text-center py-5 text-muted">
                                            <i class="fas fa-inbox fa-3x mb-3 text-light"></i>
                                            <h5>Aucun événement trouvé</h5>
                                            <p>Vous n'avez pas encore d'événements affichant des statistiques.</p>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Loading State -->
        <div class="row" *ngIf="loading">
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-3 text-muted fw-medium">Chargement de vos statistiques...</p>
            </div>
        </div>

        <!-- Error State -->
        <div class="row" *ngIf="error">
            <div class="col-12">
                <div class="alert alert-danger shadow-sm rounded-4 d-flex align-items-center" role="alert">
                    <i class="fas fa-exclamation-triangle fa-2x me-3"></i>
                    <div>
                        <h5 class="alert-heading fw-bold mb-1">Erreur de chargement</h5>
                        <p class="mb-0">{{ error }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `,
    styles: [`
    /* Typography & Utilities */
    .font-size-sm { font-size: 0.85rem; font-weight: 600; letter-spacing: 0.5px; }
    .transition-all { transition: all 0.3s ease; }

    /* Header Styling */
    .dashboard-header-card {
        background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
        color: white;
    }
    .decoration-circle-1 {
        position: absolute; width: 150px; height: 150px; background: rgba(255, 255, 255, 0.1);
        border-radius: 50%; top: -50px; right: -20px;
    }
    .decoration-circle-2 {
        position: absolute; width: 300px; height: 300px; background: rgba(255, 255, 255, 0.05);
        border-radius: 50%; bottom: -150px; right: 10%;
    }
    .dashboard-title { font-weight: 800; font-size: 1.8rem; letter-spacing: -0.5px; margin-bottom: 0.5rem; }
    .dashboard-subtitle { font-size: 1rem; opacity: 0.85; margin: 0; }

    /* KPI Cards */
    .kpi-card { background: white; border: 1px solid rgba(0,0,0,0.05); transition: transform 0.3s ease, box-shadow 0.3s ease; }
    .kpi-card:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(0,0,0,0.08) !important; }
    .kpi-icon-wrapper { display: inline-flex; align-items: center; justify-content: center; width: 50px; height: 50px; border-radius: 12px; font-size: 1.5rem; }
    .stats-events .kpi-icon-wrapper { background-color: rgba(13, 110, 253, 0.1); }
    .stats-tickets .kpi-icon-wrapper { background-color: rgba(25, 135, 84, 0.1); }
    .stats-revenue .kpi-icon-wrapper { background-color: rgba(255, 193, 7, 0.1); }
    .kpi-value { font-size: 2.2rem; font-weight: 800; color: #2c3e50; letter-spacing: -1px; }

    /* Table Styling */
    .table-card { border: 1px solid rgba(0,0,0,0.05); border-radius: 1rem; }
    .custom-table th { border-bottom: 2px solid #f1f5f9; padding-bottom: 1rem; padding-top: 1rem; color: #64748b; }
    .custom-table td { border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
    .custom-table tbody tr:hover { background-color: #f8fafc; }
    .bg-light-primary { background-color: rgba(13, 110, 253, 0.08); }
    .bg-primary-subtle { background-color: rgba(13, 110, 253, 0.1) !important; color: #0d6efd !important; font-weight: 600; }
    .bg-success-subtle { background-color: rgba(25, 135, 84, 0.1) !important; color: #198754 !important; font-weight: 600; padding: 0.4rem 0.8rem; }
    .bg-secondary-subtle { background-color: rgba(108, 117, 125, 0.1) !important; color: #6c757d !important; font-weight: 600; padding: 0.4rem 0.8rem; }
    `]
})
export class EventStatisticsComponent implements OnInit {
    statistics: EventStatistics[] = [];
    loading = true;
    error = '';

    totalTickets = 0;
    totalRevenue = 0;
    avgTicketPrice = 0;

    constructor(private eventService: EventService) { }

    ngOnInit(): void {
        this.loadStatistics();
    }

    loadStatistics(): void {
        this.loading = true;
        this.error = '';

        this.eventService.getMyStatistics().subscribe({
            next: (data) => {
                this.statistics = data;
                this.calculateKPIs();
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching statistics', err);
                this.error = 'Impossible de charger les statistiques. Vérifiez que vous êtes bien connecté et que le backend est actif.';
                this.loading = false;
            }
        });
    }

    private calculateKPIs(): void {
        let count = 0;
        let rev = 0;

        this.statistics.forEach(s => {
            count += s.ticketsSold;
            rev += s.totalRevenue;
        });

        this.totalTickets = count;
        this.totalRevenue = rev;
        this.avgTicketPrice = count > 0 ? rev / count : 0;
    }
}
