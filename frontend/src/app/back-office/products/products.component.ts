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

  editProduct(product: Product): void {
    const newName = window.prompt("Modify Product Name:", product.name);
    if (newName === null || newName.trim() === "") return;
    if (newName === product.name) return;

    let updatedProduct = { ...product };
    updatedProduct.name = newName.trim();

    this.productService.updateProduct(updatedProduct, product.id!).subscribe({
      next: (res) => {
        product.name = res.name;
        alert("✅ Product updated successfully!");
      },
      error: (err) => {
        console.error(err);
        alert("❌ Error updating product.");
      }
    });
  }

  deleteProduct(product: Product): void {
    const confirmed = window.confirm(`Are you sure you want to delete the product '${product.name}'?\nThis action will completely remove it from the seller's store and the marketplace.`);
    if (confirmed && product.id) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== product.id);
          alert("✅ Product deleted successfully!");
        },
        error: (err) => {
          console.error(err);
          const backendMsg = err.error?.error || err.message;
          alert("❌ Error deleting product: " + backendMsg);
        }
      });
    }
  }
}
