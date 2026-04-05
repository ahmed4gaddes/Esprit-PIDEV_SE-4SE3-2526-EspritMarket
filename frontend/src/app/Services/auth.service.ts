import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class AuthService {

 
  // private apiUrl = 'http://localhost:8081/auth';

  constructor(private http: HttpClient) {}

  // Login -> stores auth token
  login(email: string, password: string) {
    return this.http.post<any>('http://localhost:8081/api/auth/login', {
      email,
      password
    });
  }

  // ✅ Sauvegarder token
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Returns current token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Clears token
  logout(): void {
    localStorage.removeItem('token');
  }
}
