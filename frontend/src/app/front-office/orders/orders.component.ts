import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  loading = false;
  message = '';

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: (data) => { this.orders = data; this.loading = false; },
      error: () => { this.message = 'Error loading orders'; this.loading = false; }
    });
  }

  cancelOrder(orderId: number) {
    this.orderService.cancelOrder(orderId).subscribe({
      next: () => { this.message = 'Order cancelled!'; this.loadOrders(); },
      error: () => { this.message = 'Cannot cancel this order'; }
    });
  }
}