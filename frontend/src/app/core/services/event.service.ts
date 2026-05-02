import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, EventStatistics, EventStatus, EventType, UserRole, DynamicPriceResponse, PricingRule } from '../models/event.model';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private apiUrl = `${environment.apiUrl}/api/events`;

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
    // NOUVEAUX KEYWORDS : Recherche pour les événements d'un Seller
    // =====================================================================
    searchMyEventsByTitle(title: string): Observable<Event[]> {
        const params = new HttpParams().set('title', title);
        return this.http.get<Event[]>(`${this.apiUrl}/search/my-events-by-title`, { params, withCredentials: true });
    }

    searchMyEventsCreatedAfter(date: string): Observable<Event[]> {
        // Date doit être au format yyyy-MM-dd
        const params = new HttpParams().set('date', date);
        return this.http.get<Event[]>(`${this.apiUrl}/search/my-events-after-date`, { params, withCredentials: true });
    }

    // =====================================================================
    // DYNAMIC PRICING
    // =====================================================================
    getCurrentPrice(eventId: number): Observable<DynamicPriceResponse> {
        return this.http.get<DynamicPriceResponse>(`${this.apiUrl}/${eventId}/current-price`);
    }

    getPricingRule(eventId: number): Observable<PricingRule> {
        return this.http.get<PricingRule>(`${this.apiUrl}/${eventId}/pricing-rule`);
    }

    createPricingRule(eventId: number, rule: Partial<PricingRule>): Observable<PricingRule> {
        return this.http.post<PricingRule>(`${this.apiUrl}/${eventId}/pricing-rule`, rule, { withCredentials: true });
    }

    updatePricingRule(eventId: number, rule: Partial<PricingRule>): Observable<PricingRule> {
        return this.http.put<PricingRule>(`${this.apiUrl}/${eventId}/pricing-rule`, rule, { withCredentials: true });
    }

    deletePricingRule(eventId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${eventId}/pricing-rule`, { withCredentials: true });
    }
}
