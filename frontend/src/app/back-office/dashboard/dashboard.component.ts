import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AdminService, AdminDailyActivity, AdminDashboardKpis } from '../../core/services/admin.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { ProductService } from '../../Services/product.service';
import { CategoryService } from '../../Services/category.service';
import { Store } from '../../models/store';
import { Product } from '../../models/product';
import { Category } from '../../models/category';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  loading = false;
  errorMessage = '';
  kpis: AdminDashboardKpis | null = null;
  chartData: AdminDailyActivity[] = [];

  /** Live catalog data from Store / Product / Category APIs */
  stores: Store[] = [];
  products: Product[] = [];
  categories: Category[] = [];
  catalogLoadNote = '';
  kpiError = '';

  constructor(
    private adminService: AdminService,
    private storeService: StoreServiceService,
    private productService: ProductService,
    private categoryService: CategoryService
  ) {
    this.loadDashboard();
  }

  loadDashboard() {
    this.loading = true;
    this.errorMessage = '';
    this.catalogLoadNote = '';
    this.kpiError = '';
    forkJoin({
      dash: this.adminService.getDashboard(7).pipe(
        catchError(() => {
          this.kpiError = 'KPI summary could not be loaded (check admin session).';
          return of({ kpis: null as AdminDashboardKpis | null, activity: [] as AdminDailyActivity[] });
        })
      ),
      stores: this.storeService.getAllStores().pipe(catchError(() => of([] as Store[]))),
      products: this.productService.getAllProducts().pipe(catchError(() => of([] as Product[]))),
      categories: this.categoryService.getAllCategories().pipe(catchError(() => of([] as Category[])))
    }).subscribe({
      next: ({ dash, stores, products, categories }) => {
        if (dash.kpis) {
          this.kpis = dash.kpis;
          this.chartData = dash.activity || [];
        }
        this.stores = stores || [];
        this.products = products || [];
        this.categories = categories || [];
        if (!this.stores.length && !this.products.length && !this.categories.length) {
          this.catalogLoadNote = 'No stores, products or categories returned from the catalog APIs.';
        }
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load admin dashboard data.';
        this.loading = false;
      }
    });
  }

  get stats() {
    const k = this.kpis;
    if (!k) {
      return [];
    }
    const catCount = this.categories.length;
    return [
      { label: 'Users', value: `${k.totalUsers}`, change: `${k.activeUsers} active`, isUp: true, icon: 'fas fa-users', colorClass: 'stat-blue' },
      { label: 'Stores & Products', value: `${k.totalStores} / ${k.totalProducts}`, change: 'stores / products (DB)', isUp: true, icon: 'fas fa-store', colorClass: 'stat-green' },
      { label: 'Categories', value: `${catCount}`, change: 'loaded from API', isUp: true, icon: 'fas fa-tags', colorClass: 'stat-purple' },
      { label: 'Events & Lives', value: `${k.totalEvents} / ${k.totalLives}`, change: 'events/lives', isUp: true, icon: 'fas fa-video', colorClass: 'stat-orange' },
      { label: 'Pending Applications', value: `${k.pendingInternshipApplications}`, change: `${k.totalInternships} internships`, isUp: true, icon: 'fas fa-briefcase', colorClass: 'stat-red' }
    ];
  }

  get maxSales(): number {
    if (!this.chartData.length) {
      return 1;
    }
    return Math.max(...this.chartData.map(d => d.newUsers + d.newApplications + d.ticketsSold), 1);
  }

  getBarHeight(value: number): number {
    return (value / this.maxSales) * 100;
  }
}
