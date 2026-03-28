import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Product } from '../../models/product';
import { CartItem } from '../models/cart-item.model';

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private items: CartItem[] = [];
    private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.items);

    constructor() {
        // Load from localStorage if present
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
            try {
                this.items = JSON.parse(savedCart);
                this.cartItemsSubject.next(this.items);
            } catch (e) {
                console.error('Failed to parse cart', e);
            }
        }
    }

    /**
     * Get the current cart items as an Observable.
     */
    getCartItems(): Observable<CartItem[]> {
        return this.cartItemsSubject.asObservable();
    }

    /**
     * Get current total item count.
     */
    getCartCount(): number {
        return this.items.reduce((acc, item) => acc + item.quantity, 0);
    }

    /**
     * Add a product to the cart.
     */
    addItem(product: Product, quantity: number = 1): void {
        const existingItem = this.items.find(item => item.product.id === product.id);
        
        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            this.items.push({ product, quantity });
        }
        
        this.saveCart();
    }

    /**
     * Remove a product from the cart completely.
     */
    removeItem(productId: number | undefined): void {
        if (!productId) return;
        this.items = this.items.filter(item => item.product.id !== productId);
        this.saveCart();
    }

    /**
     * Update quantity for an existing cart item.
     */
    updateQuantity(productId: number | undefined, quantity: number): void {
        if (!productId) return;
        const item = this.items.find(ci => ci.product.id === productId);
        if (!item) return;

        if (quantity <= 0) {
            this.removeItem(productId);
            return;
        }

        item.quantity = quantity;
        this.saveCart();
    }

    /**
     * Calculate total price of cart.
     */
    getTotalPrice(): number {
        return this.items.reduce((acc, item) => acc + (item.product.price * item.quantity), 0);
    }

    /**
     * Clear the entire cart.
     */
    clearCart(): void {
        this.items = [];
        this.saveCart();
    }

    private saveCart(): void {
        localStorage.setItem('cart', JSON.stringify(this.items));
        this.cartItemsSubject.next([...this.items]);
    }
}
