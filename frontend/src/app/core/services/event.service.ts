import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, EventStatistics, EventStatus, EventType, UserRole } from '../models/event.model';

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

    getByStore(storeId: number): Observable<Event[]> {
        return this.http.get<Event[]>(`${this.apiUrl}/store/${storeId}`);
    }

    getByService(serviceId: number): Observable<Event[]> {
        return this.http.get<Event[]>(`${this.apiUrl}/service/${serviceId}`);
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

    // =====================================================================
    // JPQL : statistiques par organisateur (JOIN Event + User + Ticket)
    // =====================================================================
    getStatisticsByOrganizer(organizerId: number): Observable<EventStatistics[]> {
        return this.http.get<EventStatistics[]>(
            `${this.apiUrl}/statistics/organizer/${organizerId}`
        );
    }

    getMyStatistics(): Observable<EventStatistics[]> {
        return this.http.get<EventStatistics[]>(`${this.apiUrl}/statistics/my-stats`, { withCredentials: true });
    }

    // =====================================================================
    // KEYWORDS : recherche par rôle de l'organisateur + statut
    // =====================================================================
    searchByOrganizerRole(role: UserRole, status: EventStatus): Observable<Event[]> {
        const params = new HttpParams()
            .set('role', role)
            .set('status', status);
        return this.http.get<Event[]>(`${this.apiUrl}/search/by-role`, { params });
    }

    // =====================================================================
    // KEYWORDS : recherche par nom de store + type
    // =====================================================================
    searchByStoreName(storeName: string, type: EventType): Observable<Event[]> {
        const params = new HttpParams()
            .set('storeName', storeName)
            .set('type', type);
        return this.http.get<Event[]>(`${this.apiUrl}/search/by-store`, { params });
    }

    // =====================================================================
    // KEYWORDS : events à venir d'un organisateur
    // =====================================================================
    getUpcomingByOrganizer(organizerId: number): Observable<Event[]> {
        return this.http.get<Event[]>(
            `${this.apiUrl}/search/upcoming/${organizerId}`
        );
    }
}

