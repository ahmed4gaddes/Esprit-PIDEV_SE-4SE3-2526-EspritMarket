import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment, PaymentRequest } from '../models/payment.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = 'http://localhost:8081/api/payments';

  constructor(private http: HttpClient) {}

  processPayment(orderId: number, request: PaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/order/${orderId}`, request, { withCredentials: true });
  }

  getPaymentByOrderId(orderId: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/order/${orderId}`, { withCredentials: true });
  }
}
