import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: any = null;
  loading = false;
  message = '';

  constructor(private cartService: CartService, private orderService: OrderService, private router: Router) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.loading = true;
    this.cartService.getMyCart().subscribe({
      next: (data) => { this.cart = data; this.loading = false; },
      error: () => { this.message = 'Error loading cart'; this.loading = false; }
    });
  }

  removeItem(itemId: number) {
    this.cartService.removeItem(itemId).subscribe({
      next: (data) => { this.cart = data; },
      error: () => { this.message = 'Error removing item'; }
    });
  }

  clearCart() {
    this.cartService.clearCart().subscribe({
      next: () => { this.loadCart(); },
      error: () => { this.message = 'Error clearing cart'; }
    });
  }

  checkout() {
    this.orderService.createOrder('Default Address', 'CARD').subscribe({
      next: () => { this.message = 'Order created!'; this.router.navigate(['/orders']); },
      error: () => { this.message = 'Error creating order'; }
    });
  }
}