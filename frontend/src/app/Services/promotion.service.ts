import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Promotion {
  id?: number;
  code: string;
  type: 'PERCENTAGE' | 'FIXED_AMOUNT';
  discountValue: number;
  expiresAt: string;
  usageLimit: number;
  usedCount?: number;
  store?: any;
}

@Injectable({
  providedIn: 'root'
})
export class PromotionService {
  private apiUrl = 'http://localhost:8081/Promotion';

  constructor(private http: HttpClient) {}

  createPromotion(promo: Promotion, storeId: number): Observable<Promotion> {
    promo.store = { id: storeId };
    return this.http.post<Promotion>(`${this.apiUrl}/add`, promo, { withCredentials: true });
  }

  getPromotionsByStore(storeId: number): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(`${this.apiUrl}/store/${storeId}`, { withCredentials: true });
  }
}
