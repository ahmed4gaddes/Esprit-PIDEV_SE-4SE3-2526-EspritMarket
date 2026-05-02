import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LiveSession, LiveSessionStatus } from '../models/live-session.model';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class LiveSessionService {

    private apiUrl = `${environment.apiUrl}/api/live-sessions`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<LiveSession[]> {
        return this.http.get<LiveSession[]>(this.apiUrl);
    }

    getById(id: number): Observable<LiveSession> {
        return this.http.get<LiveSession>(`${this.apiUrl}/${id}`);
    }

    getByEvent(eventId: number): Observable<LiveSession[]> {
        return this.http.get<LiveSession[]>(`${this.apiUrl}/event/${eventId}`);
    }

    getByStore(storeId: number): Observable<LiveSession[]> {
        return this.http.get<LiveSession[]>(`${this.apiUrl}/store/${storeId}`);
    }

    getByService(serviceId: number): Observable<LiveSession[]> {
        return this.http.get<LiveSession[]>(`${this.apiUrl}/service/${serviceId}`);
    }

    getByCreator(userId: number): Observable<LiveSession[]> {
        return this.http.get<LiveSession[]>(`${this.apiUrl}/user/${userId}`);
    }

    create(liveSession: Partial<LiveSession>, eventId?: number): Observable<LiveSession> {
        let params = new HttpParams();
        if (eventId) {
            params = params.set('eventId', eventId.toString());
        }
        return this.http.post<LiveSession>(this.apiUrl, liveSession, { params });
    }

    update(id: number, liveSession: Partial<LiveSession>): Observable<LiveSession> {
        return this.http.put<LiveSession>(`${this.apiUrl}/${id}`, liveSession);
    }

    updateStatus(id: number, status: LiveSessionStatus): Observable<LiveSession> {
        return this.http.put<LiveSession>(`${this.apiUrl}/${id}/status?status=${status}`, {});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
