import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent {
  categories = [
    { id: 1, name: 'Design & Art', icon: 'fas fa-palette', products: 250, status: 'Active', description: 'Logos, illustrations, graphics' },
    { id: 2, name: 'Development', icon: 'fas fa-code', products: 180, status: 'Active', description: 'Websites, applications, scripts' },
    { id: 3, name: 'Courses & Tutoring', icon: 'fas fa-graduation-cap', products: 120, status: 'Active', description: 'Academic support, training' },
    { id: 4, name: 'Handmade', icon: 'fas fa-hand-holding-heart', products: 300, status: 'Active', description: 'Accessories, jewelry, decor' },
    { id: 5, name: 'Tech & Gadgets', icon: 'fas fa-laptop', products: 90, status: 'Inactive', description: 'Electronics, accessories' },
    { id: 6, name: 'Services', icon: 'fas fa-briefcase', products: 150, status: 'Active', description: 'Consulting, marketing, writing' }
  ];
}
