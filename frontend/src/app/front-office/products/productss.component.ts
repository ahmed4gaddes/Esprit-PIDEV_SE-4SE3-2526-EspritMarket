import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../Services/product.service';
import { CategoryService } from '../../Services/category.service';
import { Product } from '../../models/product';

@Component({
  selector: 'app-productss',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './productss.component.html',
  styleUrl: './productss.component.css'
})
export class ProductssComponent implements OnInit {

  products: Product[] = [];
  categories: any[] = [];

  // Filter properties
  searchName: string = '';
  searchCategoryId?: number;
  searchMinPrice?: number;
  searchMaxPrice?: number;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (data: any[]) => this.categories = data,
      error: (err) => console.error('Failed to load categories', err)
    });
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (data: Product[]) => {
        this.products = data;
        console.log('✅ Products loaded:', data);
      },
      error: (err) => {
        console.error('❌ Error loading products:', err);
        alert('Erreur lors du chargement des produits');
      }
    });
  }

  onSearch(): void {
    this.productService.searchProducts(this.searchName, this.searchCategoryId, this.searchMinPrice, this.searchMaxPrice).subscribe({
      next: (data: Product[]) => {
        this.products = data;
      },
      error: (err) => {
        console.error('❌ Search error:', err);
        alert('Erreur lors de la recherche des produits');
      }
    });
  }

  onReset(): void {
    this.searchName = '';
    this.searchCategoryId = undefined;
    this.searchMinPrice = undefined;
    this.searchMaxPrice = undefined;
    this.loadProducts();
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

  viewRecommendations(product: Product): void {
    if (product.id) {
      this.router.navigate(['/user/products/recommendations', product.id]);
    }
  }

  getActiveCount(): number { return this.products.filter(p => p.active).length; }
  getInactiveCount(): number { return this.products.filter(p => !p.active).length; }
  onImgError(event: Event) {
    const element = event.target as HTMLImageElement;
    element.src = 'assets/default-image.png'; // image par défaut
  }
}
