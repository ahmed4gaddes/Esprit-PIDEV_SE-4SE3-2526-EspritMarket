import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rate } from '../models/rate';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RateService {

  private apiUrl = `${environment.apiUrl}/api/rates`;

  constructor(private http: HttpClient) {}

  addRate(ratedUserId: number, rate: Rate): Observable<Rate> {
    const params = new HttpParams().set('ratedUserId', ratedUserId);
    return this.http.post<Rate>(this.apiUrl, rate, { params, withCredentials: true });
  }

  getRatesForUser(ratedUserId: number): Observable<Rate[]> {
    return this.http.get<Rate[]>(`${this.apiUrl}/user/${ratedUserId}`);
  }

  getAverageRating(ratedUserId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/${ratedUserId}/average`);
  }

  getRatesByRater(raterId: number): Observable<Rate[]> {
    return this.http.get<Rate[]>(`${this.apiUrl}/rater/${raterId}`);
  }

  update(id: number, rate: Rate): Observable<Rate> {
    return this.http.put<Rate>(`${this.apiUrl}/${id}`, rate, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}
