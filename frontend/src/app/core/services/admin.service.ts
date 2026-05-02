import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedResponse, User } from './user.service';
import { environment } from '../../../environments/environment';

export interface AdminDashboardKpis {
  totalUsers: number;
  activeUsers: number;
  totalStores: number;
  totalProducts: number;
  totalEvents: number;
  totalLives: number;
  totalInternships: number;
  pendingInternshipApplications: number;
  ticketsSold: number;
  ticketRevenue: number;
}

export interface AdminDailyActivity {
  date: string;
  newUsers: number;
  newApplications: number;
  ticketsSold: number;
}

export interface AdminDashboardResponse {
  kpis: AdminDashboardKpis;
  activity: AdminDailyActivity[];
}

export interface InternshipApplicationAdmin {
  id: number;
  internshipId: number;
  internshipTitle: string;
  applicantId: number;
  applicantName: string;
  applicantEmail: string;
  cvUrl: string;
  coverLetter: string;
  status: string;
  appliedAt: string;
  reviewedAt?: string;
  reviewerComment?: string;
}

export interface AdminAuditLog {
  id: number;
  action: string;
  actorUserId: number;
  actorEmail: string;
  targetUserId?: number;
  targetUserEmail?: string;
  details?: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/api/admin`;

  constructor(private http: HttpClient) { }

  getDashboard(days = 7): Observable<AdminDashboardResponse> {
    const params = new HttpParams().set('days', days);
    return this.http.get<AdminDashboardResponse>(`${this.apiUrl}/dashboard`, { params });
  }

  getUsers(filters?: { role?: string; active?: string; q?: string; page?: number; size?: number }): Observable<PagedResponse<User>> {
    let params = new HttpParams();
    if (filters?.role) {
      params = params.set('role', filters.role);
    }
    if (filters?.active && filters.active !== 'ALL') {
      params = params.set('active', filters.active === 'ACTIVE');
    }
    if (filters?.q?.trim()) {
      params = params.set('q', filters.q.trim());
    }
    params = params.set('page', filters?.page ?? 0);
    params = params.set('size', filters?.size ?? 10);
    return this.http.get<PagedResponse<User>>(`${this.apiUrl}/users`, { params });
  }

  toggleUserStatus(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${id}/toggle-status`, {});
  }

  updateUserRole(id: number, role: string): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${id}/role`, { role });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
  }

  getInternshipApplications(filters?: {
    status?: string;
    internshipId?: number;
    companyId?: number;
    q?: string;
    page?: number;
    size?: number;
  }): Observable<PagedResponse<InternshipApplicationAdmin>> {
    let params = new HttpParams();
    if (filters?.status && filters.status !== 'ALL') {
      params = params.set('status', filters.status);
    }
    if (filters?.internshipId) {
      params = params.set('internshipId', filters.internshipId);
    }
    if (filters?.companyId) {
      params = params.set('companyId', filters.companyId);
    }
    if (filters?.q?.trim()) {
      params = params.set('q', filters.q.trim());
    }
    params = params.set('page', filters?.page ?? 0);
    params = params.set('size', filters?.size ?? 10);
    return this.http.get<PagedResponse<InternshipApplicationAdmin>>(`${this.apiUrl}/internship-applications`, { params });
  }

  getAuditLogs(filters?: { action?: string; q?: string; page?: number; size?: number }): Observable<PagedResponse<AdminAuditLog>> {
    let params = new HttpParams();
    if (filters?.action?.trim()) {
      params = params.set('action', filters.action.trim());
    }
    if (filters?.q?.trim()) {
      params = params.set('q', filters.q.trim());
    }
    params = params.set('page', filters?.page ?? 0);
    params = params.set('size', filters?.size ?? 20);
    return this.http.get<PagedResponse<AdminAuditLog>>(`${this.apiUrl}/audit-logs`, { params });
  }
}
