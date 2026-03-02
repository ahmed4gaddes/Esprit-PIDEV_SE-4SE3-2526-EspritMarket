import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, EventStatus, EventType } from '../models/event.model';

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private apiUrl = 'http://localhost:8081/api/events'; // Adjust port if needed

    constructor(private http: HttpClient) { }

    getAll(): Observable<Event[]> {
        return this.http.get<Event[]>(this.apiUrl);
    }

    getById(id: number): Observable<Event> {
        return this.http.get<Event>(`${this.apiUrl}/${id}`);
    }

    getByType(type: EventType): Observable<Event[]> {
        return this.http.get<Event[]>(`${this.apiUrl}/type/${type}`);
    }

    create(event: Partial<Event>): Observable<Event> {
        return this.http.post<Event>(this.apiUrl, event);
    }

    update(id: number, event: Partial<Event>): Observable<Event> {
        return this.http.put<Event>(`${this.apiUrl}/${id}`, event);
    }

    updateStatus(id: number, status: EventStatus): Observable<Event> {
        return this.http.put<Event>(`${this.apiUrl}/${id}/status?status=${status}`, {});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
