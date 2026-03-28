import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';
import { Product } from '../../models/product';

describe('CartService', () => {
  let service: CartService;

  const product = (over: Partial<Product> = {}): Product => ({
    name: 'P',
    price: 10,
    stock: 5,
    active: true,
    id: 1,
    ...over
  });

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [CartService]
    });
    service = TestBed.inject(CartService);
  });

  afterEach(() => {
    localStorage.removeItem('cart');
  });

  it('should start empty', () => {
    expect(service.getCartCount()).toBe(0);
    expect(service.getTotalPrice()).toBe(0);
  });

  it('addItem should append and merge quantity', () => {
    service.addItem(product({ id: 1 }), 2);
    expect(service.getCartCount()).toBe(2);
    service.addItem(product({ id: 1 }), 1);
    expect(service.getCartCount()).toBe(3);
    expect(service.getTotalPrice()).toBe(30);
  });

  it('removeItem should drop line', () => {
    service.addItem(product({ id: 5 }), 1);
    service.removeItem(5);
    expect(service.getCartCount()).toBe(0);
  });

  it('removeItem should no-op when id missing', () => {
    service.addItem(product({ id: 1 }), 1);
    service.removeItem(undefined);
    expect(service.getCartCount()).toBe(1);
  });

  it('updateQuantity should change qty or remove', () => {
    service.addItem(product({ id: 2 }), 2);
    service.updateQuantity(2, 4);
    expect(service.getCartCount()).toBe(4);
    service.updateQuantity(2, 0);
    expect(service.getCartCount()).toBe(0);
  });

  it('clearCart should reset', () => {
    service.addItem(product(), 1);
    service.clearCart();
    expect(service.getCartCount()).toBe(0);
  });

  it('should persist cart to localStorage', () => {
    service.addItem(product({ id: 99 }), 2);
    const raw = localStorage.getItem('cart');
    expect(raw).toBeTruthy();
    const parsed = JSON.parse(raw!);
    expect(parsed[0].quantity).toBe(2);
  });

  it('should load cart from localStorage on init', () => {
    localStorage.setItem(
      'cart',
      JSON.stringify([{ product: product({ id: 7, price: 5 }), quantity: 2 }])
    );
    const s2 = new CartService();
    expect(s2.getCartCount()).toBe(2);
    expect(s2.getTotalPrice()).toBe(10);
  });
});
