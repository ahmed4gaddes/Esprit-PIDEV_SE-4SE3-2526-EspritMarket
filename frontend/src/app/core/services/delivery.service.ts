import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Delivery, DeliveryRequest } from '../models/delivery.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = `${environment.apiUrl}/api/deliveries`;

  constructor(private http: HttpClient) {}

  initiateDelivery(orderId: number, request: DeliveryRequest): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.apiUrl}/order/${orderId}`, request, { withCredentials: true });
  }

  getDeliveryByOrderId(orderId: number): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/order/${orderId}`, { withCredentials: true });
  }
}
