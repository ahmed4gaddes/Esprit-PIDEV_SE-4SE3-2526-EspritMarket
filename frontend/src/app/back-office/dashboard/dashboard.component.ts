import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  stats = [
    { label: 'Total Revenue', value: '124,500 TND', change: '+12.5%', isUp: true, icon: 'fas fa-dollar-sign', colorClass: 'stat-green' },
    { label: 'Total Sales', value: '1,250', change: '+8.2%', isUp: true, icon: 'fas fa-shopping-bag', colorClass: 'stat-blue' },
    { label: 'New Users', value: '450', change: '-2.4%', isUp: false, icon: 'fas fa-users', colorClass: 'stat-red' },
    { label: 'Conversion Rate', value: '3.2%', change: '+0.5%', isUp: true, icon: 'fas fa-chart-line', colorClass: 'stat-orange' }
  ];

  chartData = [
    { name: 'Mon', sales: 4000 },
    { name: 'Tue', sales: 3000 },
    { name: 'Wed', sales: 2000 },
    { name: 'Thu', sales: 2780 },
    { name: 'Fri', sales: 1890 },
    { name: 'Sat', sales: 2390 },
    { name: 'Sun', sales: 3490 }
  ];

  recentOrders = [
    { id: '#1234', student: 'Ahmed Trabelsi', product: 'React Course', amount: '85 TND', status: 'Completed', date: '2 mins ago', statusClass: 'status-completed' },
    { id: '#1235', student: 'Sonia Ben Ali', product: 'Handmade Bag', amount: '120 TND', status: 'Pending', date: '15 mins ago', statusClass: 'status-pending' },
    { id: '#1236', student: 'Mehdi Kamoun', product: 'Logo Design', amount: '150 TND', status: 'Completed', date: '1 hour ago', statusClass: 'status-completed' },
    { id: '#1237', student: 'Yassine Jlassi', product: 'Tech Gadget', amount: '320 TND', status: 'Processing', date: '3 hours ago', statusClass: 'status-processing' }
  ];

  get maxSales(): number {
    return Math.max(...this.chartData.map(d => d.sales));
  }

  getBarHeight(sales: number): number {
    return (sales / this.maxSales) * 100;
  }
}
