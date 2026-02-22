import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

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

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });
    }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/users`, { headers: this.getHeaders() });
    }

    toggleUserStatus(id: number): Observable<User> {
        return this.http.put<User>(`${this.apiUrl}/users/${id}/toggle-status`, {}, { headers: this.getHeaders() });
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/users/${id}`, { headers: this.getHeaders() });
    }
}
