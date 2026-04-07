import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import {
  Event as MarketEvent,
  EventStatistics,
  EventStatus,
  EventType,
  UserRole
} from '../../core/models/event.model';

@Component({
  selector: 'app-event-statistics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './event-statistics.component.html',
  styleUrls: ['./event-statistics.component.css']
})
export class EventStatisticsComponent implements OnInit {

  // === JPQL : Statistiques organisateur ===
  organizerId: number = 1;
  statistics: EventStatistics[] = [];
  statsLoading = false;
  statsError = '';
  totalRevenue = 0;
  totalTickets = 0;

  // === Keywords : Recherche par rôle + statut ===
  selectedRole: UserRole = UserRole.COMPANY;
  selectedStatus: EventStatus = EventStatus.UPCOMING;
  roleSearchResults: MarketEvent[] = [];
  roleSearchLoading = false;

  // === Keywords : Recherche par Store + type ===
  storeName = '';
  selectedType: EventType = EventType.WORKSHOP_EVENT;
  storeSearchResults: MarketEvent[] = [];
  storeSearchLoading = false;

  // Enums pour les selects
  roles = Object.values(UserRole);
  statuses = Object.values(EventStatus);
  eventTypes = Object.values(EventType);

  constructor(private eventService: EventService) {}

  ngOnInit(): void {}

  // =====================================================================
  // JPQL : Charger les statistiques par organisateur
  // =====================================================================
  loadStatistics(): void {
    if (!this.organizerId || this.organizerId <= 0) return;

    this.statsLoading = true;
    this.statsError = '';
    this.eventService.getStatisticsByOrganizer(this.organizerId).subscribe({
      next: (data) => {
        this.statistics = data ?? [];
        this.totalRevenue = this.statistics.reduce((sum, s) => sum + (s.totalRevenue || 0), 0);
        this.totalTickets = this.statistics.reduce((sum, s) => sum + (s.ticketsSold || 0), 0);
        this.statsLoading = false;
      },
      error: (err) => {
        console.error('Error loading statistics', err);
        this.statsError = 'Erreur lors du chargement des statistiques.';
        this.statsLoading = false;
      }
    });
  }

  // =====================================================================
  // Keywords : Recherche par rôle de l'organisateur + statut
  // =====================================================================
  searchByRole(): void {
    this.roleSearchLoading = true;
    this.eventService.searchByOrganizerRole(this.selectedRole, this.selectedStatus).subscribe({
      next: (data) => {
        this.roleSearchResults = data ?? [];
        this.roleSearchLoading = false;
      },
      error: (err) => {
        console.error('Error searching by role', err);
        this.roleSearchLoading = false;
      }
    });
  }

  // =====================================================================
  // Keywords : Recherche par nom de store + type
  // =====================================================================
  searchByStore(): void {
    if (!this.storeName.trim()) return;

    this.storeSearchLoading = true;
    this.eventService.searchByStoreName(this.storeName, this.selectedType).subscribe({
      next: (data) => {
        this.storeSearchResults = data ?? [];
        this.storeSearchLoading = false;
      },
      error: (err) => {
        console.error('Error searching by store', err);
        this.storeSearchLoading = false;
      }
    });
  }

  // Helpers
  formatType(type: string): string {
    return type.replace('_EVENT', '').replace(/_/g, ' ');
  }
}
