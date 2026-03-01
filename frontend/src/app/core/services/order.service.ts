import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  createOrder(deliveryAddress: string, paymentMethod: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/orders`,
      { deliveryAddress, paymentMethod },
      { headers: this.getHeaders() });
  }

  getMyOrders(): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/my`, { headers: this.getHeaders() });
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/orders/${orderId}/cancel`, {}, { headers: this.getHeaders() });
  }

  getDelivery(orderId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/deliveries/order/${orderId}`, { headers: this.getHeaders() });
  }
}