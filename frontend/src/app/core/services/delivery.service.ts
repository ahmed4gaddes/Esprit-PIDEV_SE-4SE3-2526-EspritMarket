import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Delivery, DeliveryRequest } from '../models/delivery.model';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = 'http://localhost:8081/api/deliveries';

  constructor(private http: HttpClient) {}

  initiateDelivery(orderId: number, request: DeliveryRequest): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.apiUrl}/order/${orderId}`, request, { withCredentials: true });
  }

  getDeliveryByOrderId(orderId: number): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/order/${orderId}`, { withCredentials: true });
  }
}
