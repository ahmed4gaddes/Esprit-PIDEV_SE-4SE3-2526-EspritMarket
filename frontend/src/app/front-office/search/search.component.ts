import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../Services/product.service';  // ← 2 niveaux
import { Product } from '../../models/product';  
@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <input
      [(ngModel)]="query"
      placeholder="Décrivez votre besoin..."
      (keyup.enter)="onSearch()" />
    <button (click)="onSearch()">Rechercher</button>

    <div *ngFor="let p of results" class="product-card">
      <h3>{{ p.name }}</h3>
      <p>{{ p.price }} TND</p>
      <p>{{ p.description }}</p>
    </div>
  `
})
export class SearchComponent {
  query   = '';
  results: Product[] = [];

  constructor(private productService: ProductService) {}

  onSearch(): void {
    this.productService.searchByAI(this.query)
      .subscribe((data: Product[]) => this.results = data);
  }
}