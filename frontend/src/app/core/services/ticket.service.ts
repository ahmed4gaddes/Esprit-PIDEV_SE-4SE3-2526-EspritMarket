import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket } from '../models/ticket.model';

@Injectable({
    providedIn: 'root'
})
export class TicketService {

    private baseUrl = 'http://localhost:8089/api';

    constructor(private http: HttpClient) { }

    create(eventId: number, request: { price: number, userId: number }): Observable<Ticket> {
        return this.http.post<Ticket>(`${this.baseUrl}/events/${eventId}/tickets`, request);
    }

    getByEvent(eventId: number): Observable<Ticket[]> {
        return this.http.get<Ticket[]>(`${this.baseUrl}/events/${eventId}/tickets`);
    }

    getByUser(userId: number): Observable<Ticket[]> {
        return this.http.get<Ticket[]>(`${this.baseUrl}/users/${userId}/tickets`);
    }

    getById(id: number): Observable<Ticket> {
        return this.http.get<Ticket>(`${this.baseUrl}/tickets/${id}`);
    }

    checkIn(id: number): Observable<Ticket> {
        return this.http.put<Ticket>(`${this.baseUrl}/tickets/${id}/check-in`, {});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/tickets/${id}`);
    }
}
