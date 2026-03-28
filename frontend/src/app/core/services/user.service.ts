import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface User {
    id: number;
    name: string;
    email: string;
    role: string;
    storeActive: boolean;
    isActive: boolean;
    phoneNumber?: string;
    createdAt?: string;
}

export interface PagedResponse<T> {
    items: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

const REQ = { withCredentials: true };

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8081/api/admin';
    private readonly allowedRoles = ['ADMIN', 'SELLER', 'CUSTOMER', 'EXPERT', 'COMPANY', 'SPONSOR'] as const;

    constructor(private http: HttpClient) { }

    getAllUsers(filters?: { role?: string; active?: string; q?: string; page?: number; size?: number }): Observable<PagedResponse<User>> {
        let params = new HttpParams();
        if (filters?.role && filters.role !== 'ALL') {
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
        return new Observable<PagedResponse<User>>(subscriber => {
            this.http.get<PagedResponse<any>>(`${this.apiUrl}/users`, { params, ...REQ }).subscribe({
                next: (resp) => {
                    const mappedItems: User[] = (resp.items || []).map((raw: any) => this.mapUserFromApi(raw));

                    subscriber.next({
                        ...resp,
                        items: mappedItems
                    });
                    subscriber.complete();
                },
                error: (err) => subscriber.error(err)
            });
        });
    }

    private mapUserFromApi(raw: any): User {
        return {
            id: raw.id,
            name: raw.name,
            email: raw.email,
            role: this.normalizeRole(raw.role),
            storeActive: !!raw.storeActive,
            isActive: raw.isActive ?? raw.active ?? false,
            phoneNumber: raw.phoneNumber,
            createdAt: raw.createdAt
        };
    }

    private normalizeRole(roleRaw: unknown): string {
        const roleValue = typeof roleRaw === 'string'
            ? roleRaw
            : (roleRaw as any)?.name;
        const cleaned = String(roleValue ?? '')
            .trim()
            .toUpperCase()
            .replace(/^ROLE_/, '');

        if (this.allowedRoles.includes(cleaned as any)) {
            return cleaned;
        }
        return cleaned || 'CUSTOMER';
    }

    toggleUserStatus(id: number): Observable<User> {
        return this.http
            .put<any>(`${this.apiUrl}/users/${id}/toggle-status`, {}, REQ)
            .pipe(map((raw) => this.mapUserFromApi(raw)));
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/users/${id}`, REQ);
    }

    updateUserRole(id: number, role: string): Observable<User> {
        return this.http
            .put<any>(`${this.apiUrl}/users/${id}/role`, { role }, REQ)
            .pipe(map((raw) => this.mapUserFromApi(raw)));
    }
}
