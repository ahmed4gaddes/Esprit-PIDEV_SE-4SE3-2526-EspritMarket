import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent {
  menuItems = [
    { icon: 'fas fa-th-large', label: 'Dashboard', path: '/admin' },
    { icon: 'fas fa-shopping-bag', label: 'Products', path: '/admin/products' },
    { icon: 'fas fa-store', label: 'Stores', path: '/admin/stores' },
    { icon: 'fas fa-users', label: 'Users', path: '/admin/users' },
    { icon: 'fas fa-tags', label: 'Categories', path: '/admin/categories' },
    { icon: 'fas fa-chart-bar', label: 'Reports', path: '/admin/reports' },
    { icon: 'fas fa-cog', label: 'Settings', path: '/admin/settings' }
  ];
}
