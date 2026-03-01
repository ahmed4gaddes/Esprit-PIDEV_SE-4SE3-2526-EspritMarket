import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8081/api/cart';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  getMyCart(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my`, { headers: this.getHeaders() });
  }

  addItem(productId: number, quantity: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/my/items`, { productId, quantity }, { headers: this.getHeaders() });
  }

  removeItem(cartItemId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/my/items/${cartItemId}`, { headers: this.getHeaders() });
  }

  clearCart(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/my/clear`, { headers: this.getHeaders() });
  }
}