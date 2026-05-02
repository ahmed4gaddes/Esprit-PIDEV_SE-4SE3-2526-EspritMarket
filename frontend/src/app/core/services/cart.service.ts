import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { CartResponse, CartItemRequest } from '../models/cart.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/api/cart`;
  
  private cartSubject = new BehaviorSubject<CartResponse | null>(null);
  public cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Load the cart from the backend.
   */
  loadCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.apiUrl, { withCredentials: true }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  /**
   * Add a product or service to the cart.
   */
  addItem(request: CartItemRequest): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/items`, request, { withCredentials: true }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  /**
   * Remove an item from the cart completely.
   */
  removeItem(itemId: number): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${this.apiUrl}/items/${itemId}`, { withCredentials: true }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  /**
   * Update quantity for an existing cart item.
   */
  updateQuantity(itemId: number, quantity: number): Observable<CartResponse> {
    return this.http.put<CartResponse>(`${this.apiUrl}/items/${itemId}?quantity=${quantity}`, {}, { withCredentials: true }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  /**
   * Clear the entire cart.
   */
  clearCart(): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${this.apiUrl}/clear`, { withCredentials: true }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  /**
   * Get current total item count synchronously based on behavior subject
   */
  getCartCount(): number {
    const cart = this.cartSubject.getValue();
    if (!cart || !cart.items) return 0;
    return cart.items.reduce((acc, item) => acc + item.quantity, 0);
  }
}
