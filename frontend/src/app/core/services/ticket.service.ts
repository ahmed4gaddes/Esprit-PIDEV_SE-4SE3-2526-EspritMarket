import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket } from '../models/ticket.model';
import { environment } from '../../../environments/environment';

const REQ = { withCredentials: true };

@Injectable({
    providedIn: 'root'
})
export class TicketService {

    private baseUrl = `${environment.apiUrl}/api`;

    constructor(private http: HttpClient) { }

    create(eventId: number, request: { price: number, userId: number }): Observable<Ticket> {
        return this.http.post<Ticket>(`${this.baseUrl}/events/${eventId}/tickets`, request, REQ);
    }

    getByEvent(eventId: number): Observable<Ticket[]> {
        return this.http.get<Ticket[]>(`${this.baseUrl}/events/${eventId}/tickets`, REQ);
    }

    getByUser(userId: number): Observable<Ticket[]> {
        return this.http.get<Ticket[]>(`${this.baseUrl}/users/${userId}/tickets`, REQ);
    }

    getById(id: number): Observable<Ticket> {
        return this.http.get<Ticket>(`${this.baseUrl}/tickets/${id}`, REQ);
    }

    checkIn(id: number): Observable<Ticket> {
        return this.http.put<Ticket>(`${this.baseUrl}/tickets/${id}/check-in`, {}, REQ);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/tickets/${id}`, REQ);
    }
}
