import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StoreService } from '../../Services/store-service';
import { Store } from '../../models/store';

@Component({
  selector: 'app-stores',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent implements OnInit {
  stores: Store[] = [];
  
  constructor(private storeService: StoreService) {}

  ngOnInit(): void {
    this.loadStores();
  }

  loadStores(): void {
  this.storeService.getMyStores().subscribe({
    next: (data: Store[]) => {
      this.stores = data;
      console.log('✅ Stores:', data);
    },
    error: (err) => {
      console.error('❌ Status:', err.status);      // 404 / 0 / 403
      console.error('❌ Message:', err.message);
      console.error('❌ Full error:', err);
    }
  });
}

  deleteStore(store: Store): void {
    if (confirm(`Supprimer ${store.name} ?`)) {
      this.storeService.deleteStore(store.id).subscribe({
        next: () => {
          this.loadStores();  // Recharger la liste
          alert('Magasin supprimé avec succès');
        },
        error: (err) => {
          console.error('Erreur suppression:', err);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }
  getActiveCount(): number {
  return this.stores.filter(s => s.active).length;
}

getInactiveCount(): number {
  return this.stores.filter(s => !s.active).length;
}

getAvatarColor(name: string): string {
  const colors = ['#c0392b', '#8e1c8e', '#2563eb', '#059669', '#d97706', '#7c3aed'];
  return colors[name.charCodeAt(0) % colors.length];
}
}