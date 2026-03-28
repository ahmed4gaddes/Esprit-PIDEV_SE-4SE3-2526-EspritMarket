import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../Services/category.service';
import { Category } from '../../models/category';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  loading = true;
  errorMessage = '';
  filterText = '';

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.errorMessage = '';
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data || [];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load categories from the server.';
        this.categories = [];
        this.loading = false;
      }
    });
  }

  get filteredCategories(): Category[] {
    const q = this.filterText.trim().toLowerCase();
    if (!q) return this.categories;
    return this.categories.filter((c) => {
      const name = (c.name || '').toLowerCase();
      const type = (c.type || '').toLowerCase();
      const store = (c.storeName || '').toLowerCase();
      const desc = (c.description || '').toLowerCase();
      return name.includes(q) || type.includes(q) || store.includes(q) || desc.includes(q);
    });
  }

  productCount(c: Category): number {
    return c.productIds?.length ?? 0;
  }

  iconClass(cat: Category): string {
    const t = (cat.type || '').toUpperCase();
    const map: Record<string, string> = {
      DIGITAL: 'fas fa-file',
      PHYSICAL: 'fas fa-box',
      SERVICE: 'fas fa-briefcase',
      EDUCATION: 'fas fa-graduation-cap',
      ART: 'fas fa-palette',
      TECH: 'fas fa-laptop-code'
    };
    return map[t] || 'fas fa-folder';
  }
}
