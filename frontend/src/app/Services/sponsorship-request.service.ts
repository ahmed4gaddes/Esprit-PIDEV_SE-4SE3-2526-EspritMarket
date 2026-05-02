import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SponsorshipDecisionPayload, SponsorshipRequest } from '../models/sponsorship-request';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SponsorshipRequestService {
  private apiUrl = `${environment.apiUrl}/api/sponsorship-requests`;

  constructor(private http: HttpClient) {}

  createOffer(payload: SponsorshipRequest): Observable<SponsorshipRequest> {
    return this.http.post<SponsorshipRequest>(this.apiUrl, payload, { withCredentials: true });
  }

  getMyCompanyOffers(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/company/me`, { withCredentials: true });
  }

  getSponsorInbox(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/sponsor/inbox`, { withCredentials: true });
  }

  getSponsorHistory(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/sponsor/history`, { withCredentials: true });
  }

  decide(id: number, payload: SponsorshipDecisionPayload): Observable<SponsorshipRequest> {
    return this.http.put<SponsorshipRequest>(`${this.apiUrl}/${id}/decision`, payload, { withCredentials: true });
  }

  getApprovedForCustomers(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/public/approved`);
  }
}

