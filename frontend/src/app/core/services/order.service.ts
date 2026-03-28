import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderRequest } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8081/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(request: OrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, request, { withCredentials: true });
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my-orders`, { withCredentials: true });
  }

  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`, { withCredentials: true });
  }
  
  cancelOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${orderId}/cancel`, {}, { withCredentials: true });
  }
}
