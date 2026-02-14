import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent {
  summaryStats = [
    { label: 'Total Revenue', value: '342,500 TND', icon: 'fas fa-dollar-sign', colorClass: 'stat-green' },
    { label: 'Orders This Month', value: '1,847', icon: 'fas fa-shopping-cart', colorClass: 'stat-blue' },
    { label: 'Active Sellers', value: '126', icon: 'fas fa-store', colorClass: 'stat-red' },
    { label: 'Avg. Order Value', value: '185 TND', icon: 'fas fa-chart-line', colorClass: 'stat-orange' }
  ];

  topProducts = [
    { name: 'Logo Design Pack', sales: 156, revenue: '23,400 TND' },
    { name: 'React Dashboard', sales: 98, revenue: '8,330 TND' },
    { name: 'Silver Necklace', sales: 87, revenue: '10,440 TND' },
    { name: 'Tech Gadget Bundle', sales: 72, revenue: '23,040 TND' },
    { name: 'Minimalist T-Shirt', sales: 65, revenue: '2,925 TND' }
  ];

  monthlyData = [
    { month: 'Sep', revenue: 28000 },
    { month: 'Oct', revenue: 35000 },
    { month: 'Nov', revenue: 42000 },
    { month: 'Dec', revenue: 55000 },
    { month: 'Jan', revenue: 48000 },
    { month: 'Feb', revenue: 62000 }
  ];

  get maxRevenue(): number {
    return Math.max(...this.monthlyData.map(d => d.revenue));
  }

  getBarHeight(revenue: number): number {
    return (revenue / this.maxRevenue) * 100;
  }
}
