import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StoreServiceService } from '../../Services/store-service.service';
import { Store } from '../../models/store';

@Component({
  selector: 'app-stores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stores.component.html',
  styleUrls: ['./stores.component.css']
})
export class StoresComponent implements OnInit {
  stores: Store[] = [];
  loading = true;
  errorMessage = '';
  filterText = '';

  constructor(private storeService: StoreServiceService) {}

  ngOnInit(): void {
    this.loadStores();
  }

  loadStores(): void {
    this.loading = true;
    this.errorMessage = '';
    this.storeService.getAllStores().subscribe({
      next: (data) => {
        this.stores = data || [];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load stores from the server.';
        this.stores = [];
        this.loading = false;
      }
    });
  }

  get filteredStores(): Store[] {
    const q = this.filterText.trim().toLowerCase();
    if (!q) return this.stores;
    return this.stores.filter((s) => {
      const name = (s.name || '').toLowerCase();
      const owner = (s.ownerName || '').toLowerCase();
      const desc = (s.description || '').toLowerCase();
      return name.includes(q) || owner.includes(q) || desc.includes(q);
    });
  }

  productCount(s: Store): number {
    return s.productIds?.length ?? 0;
  }

  categorySummary(s: Store): string {
    if (s.categoryNames?.length) return s.categoryNames.join(', ');
    return '—';
  }

  statusLabel(s: Store): string {
    return s.active ? 'Active' : 'Inactive';
  }
}
