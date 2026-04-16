import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../Services/product.service';
import { Product } from '../../models/product';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-seller-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seller-analytics.component.html',
  styleUrl: './seller-analytics.component.css'
})
export class SellerAnalyticsComponent implements OnInit, AfterViewInit {
  @ViewChild('categoryChart') categoryChartRef!: ElementRef;
  @ViewChild('stockChart') stockChartRef!: ElementRef;

  products: Product[] = [];
  isLoading = true;

  categoryChart: Chart | null = null;
  stockChart: Chart | null = null;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.isLoading = false;
        
        // Wait for Angular to render the *ngIf="!isLoading" block before accessing ViewChild
        setTimeout(() => {
           if (this.categoryChartRef && this.stockChartRef) {
              this.renderCharts();
           }
        }, 50);
      },
      error: (err) => {
        console.error('Failed to load products for analytics', err);
        this.isLoading = false;
      }
    });
  }

  ngAfterViewInit(): void {
     if (!this.isLoading && this.products.length > 0) {
        this.renderCharts();
     }
  }

  renderCharts(): void {
    this.renderCategoryChart();
    this.renderStockAlertChart();
  }

  renderCategoryChart(): void {
    if (this.categoryChart) {
      this.categoryChart.destroy();
    }

    const categoryCounts: { [key: string]: number } = {};
    for (const p of this.products) {
      const catName = p.categoryName || 'Unknown';
      categoryCounts[catName] = (categoryCounts[catName] || 0) + 1;
    }

    const labels = Object.keys(categoryCounts);
    const data = Object.values(categoryCounts);
    
    // Fallback colors for default categories
    const backgroundColors = [
      '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
    ];

    const ctx = this.categoryChartRef.nativeElement.getContext('2d');
    this.categoryChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: backgroundColors.slice(0, labels.length)
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' },
          title: {
            display: true,
            text: 'Products by Category',
            font: { size: 16 }
          }
        }
      }
    });
  }

  renderStockAlertChart(): void {
    if (this.stockChart) {
      this.stockChart.destroy();
    }

    // Get bottom 5 stock items
    const sortedProducts = [...this.products].sort((a, b) => a.stock - b.stock);
    const topShortages = sortedProducts.filter(p => p.stock < 100).slice(0, 10); // Show max 10 products with stock < 100

    const labels = topShortages.map(p => p.name);
    const data = topShortages.map(p => p.stock);

    const backgroundColors = data.map(stock => {
      if (stock <= 0) return '#dc3545'; // Danger Red
      if (stock <= 5) return '#fd7e14'; // Warning Orange
      return '#ffc107'; // Warning Yellow
    });

    const ctx = this.stockChartRef.nativeElement.getContext('2d');
    this.stockChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Current Stock',
          data: data,
          backgroundColor: backgroundColors,
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true }
        },
        plugins: {
          legend: { display: false },
          title: {
            display: true,
            text: 'Stock Restock Alerts (Lowest quantity)',
            font: { size: 16 }
          }
        }
      }
    });
  }

  getActiveCount(): number {
    return this.products.filter(p => p.active).length;
  }

  getLowStockCount(): number {
    // Arbitrary threshold for low stock alert
    return this.products.filter(p => p.stock < 100).length;
  }
}
