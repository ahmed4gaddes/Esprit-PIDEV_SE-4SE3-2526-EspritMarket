import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../Services/product.service';
import { Product } from '../../models/product';

@Component({
  selector: 'app-productss',
  standalone: true,
 imports: [CommonModule, RouterModule],
  templateUrl: './productss.component.html',
  styleUrl: './productss.component.css'
})
export class ProductssComponent implements OnInit {

  products: Product[] = [];

  constructor(
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (data: Product[]) => {
        this.products = data;
        console.log('✅ Products loaded:', data);
      },
      error: (err) => {
        console.error('❌ Status:', err.status);
        console.error('❌ Error:', err.message);
        alert('Erreur lors du chargement des produits');
      }
    });
  }

  deleteProduct(id: number): void {
    if (confirm('Supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== id);
        },
        error: (err) => {
          console.error('❌ Delete error:', err);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  editProduct(id: number): void {
    this.router.navigate(['/admin/products/edit', id]);
  }

  getActiveCount(): number   { return this.products.filter(p => p.active).length; }
  getInactiveCount(): number { return this.products.filter(p => !p.active).length; }
}