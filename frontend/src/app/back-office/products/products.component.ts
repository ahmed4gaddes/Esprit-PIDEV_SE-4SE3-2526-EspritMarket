import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../Services/product.service';
import { Product } from '../../models/product';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  errorMessage = '';
  filterText = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.products = data || [];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load products from the server.';
        this.products = [];
        this.loading = false;
      }
    });
  }

  get filteredProducts(): Product[] {
    const q = this.filterText.trim().toLowerCase();
    if (!q) return this.products;
    return this.products.filter((p) => {
      const name = (p.name || '').toLowerCase();
      const cat = (p.categoryName || '').toLowerCase();
      const store = (p.storeName || '').toLowerCase();
      return name.includes(q) || cat.includes(q) || store.includes(q);
    });
  }

  statusLabel(p: Product): string {
    return p.active ? 'Active' : 'Inactive';
  }
}
