import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CartService } from './cart.service';
import { CartResponse } from '../models/cart.model';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;

  const mockCart: CartResponse = {
    id: 1,
    userId: 1,
    items: [
      { id: 10, quantity: 2, unitPrice: 15, totalPrice: 30, productId: 1, productName: 'Product A' },
      { id: 11, quantity: 1, unitPrice: 50, totalPrice: 50, serviceId: 5, serviceName: 'Service B' }
    ],
    subtotal: 80,
    deliveryFee: 7,
    total: 87
  };

  const emptyCart: CartResponse = {
    id: 1,
    userId: 1,
    items: [],
    subtotal: 0,
    deliveryFee: 0,
    total: 0
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CartService]
    });
    service = TestBed.inject(CartService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getCartCount should return 0 when no cart loaded', () => {
    expect(service.getCartCount()).toBe(0);
  });

  it('loadCart should fetch cart from backend and update subject', () => {
    service.loadCart().subscribe(cart => {
      expect(cart).toEqual(mockCart);
      expect(cart.items.length).toBe(2);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/cart');
    expect(req.request.method).toBe('GET');
    req.flush(mockCart);

    expect(service.getCartCount()).toBe(3); // 2 + 1
  });

  it('addItem should POST and update cart', () => {
    service.addItem({ productId: 1, quantity: 2 }).subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/cart/items');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ productId: 1, quantity: 2 });
    req.flush(mockCart);
  });

  it('removeItem should DELETE and update cart', () => {
    service.removeItem(10).subscribe(cart => {
      expect(cart.items.length).toBe(0);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/cart/items/10');
    expect(req.request.method).toBe('DELETE');
    req.flush(emptyCart);
  });

  it('updateQuantity should PUT and update cart', () => {
    service.updateQuantity(10, 5).subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/cart/items/10?quantity=5');
    expect(req.request.method).toBe('PUT');
    req.flush(mockCart);
  });

  it('clearCart should DELETE /clear and update cart', () => {
    service.clearCart().subscribe(cart => {
      expect(cart.items.length).toBe(0);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/cart/clear');
    expect(req.request.method).toBe('DELETE');
    req.flush(emptyCart);

    expect(service.getCartCount()).toBe(0);
  });
});
