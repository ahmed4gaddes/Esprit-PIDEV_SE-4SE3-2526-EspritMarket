import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoyaltyService {
  private apiUrl = 'http://localhost:8081/Loyalty';

  constructor(private http: HttpClient) {}

  getMyLoyaltyPoints(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/my`, { withCredentials: true });
  }
}
