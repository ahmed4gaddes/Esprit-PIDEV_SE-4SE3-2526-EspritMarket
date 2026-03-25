import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../Services/category.service'; // ✅ adapter
import { Category } from '../../models/category';             // ✅ adapter

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category.component.html',
  styleUrl: './category.component.css'
})
export class CategoryComponent {
  categories: Category[] = [];
  categoryTypes: string[] = ['DIGITAL', 'PHYSICAL', 'SERVICE', 'EDUCATION', 'ART', 'TECH'];

  constructor(
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        console.log('✅ Categories:', data);
      },
      error: (err) => console.error('❌', err.status, err.url)
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Delete this category?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => this.categories = this.categories.filter(c => c.id !== id),
        error: (err) => console.error('❌ Delete error:', err)
      });
    }
  }

  editCategory(id: number): void {
    this.router.navigate(['/admin/categories/edit', id]);
  }

  getTypeCount(type: string): number {
    return this.categories.filter(c => c.type === type).length;
  }
}