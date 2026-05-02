import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Times } from '../models/times';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TimesService {

  private apiUrl = `${environment.apiUrl}/api/times`;

  constructor(private http: HttpClient) {}

  addTimes(liveSessionId: number, times: Times): Observable<Times> {
    const params = new HttpParams().set('liveSessionId', liveSessionId);
    return this.http.post<Times>(this.apiUrl, times, { params });
  }

  getAll(): Observable<Times[]> {
    return this.http.get<Times[]>(this.apiUrl);
  }

  getByLiveSession(liveSessionId: number): Observable<Times[]> {
    return this.http.get<Times[]>(`${this.apiUrl}/live-session/${liveSessionId}`);
  }

  getById(id: number): Observable<Times> {
    return this.http.get<Times>(`${this.apiUrl}/${id}`);
  }

  update(id: number, times: Times): Observable<Times> {
    return this.http.put<Times>(`${this.apiUrl}/${id}`, times);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
