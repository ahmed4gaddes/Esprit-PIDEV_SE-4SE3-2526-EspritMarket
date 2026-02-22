import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private tokenKey = 'auth_token';
  private userRoleKey = 'user_role';
  private userNameKey = 'user_name';
  private userEmailKey = 'user_email';

  private currentStateSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.currentStateSubject.asObservable();

  private currentNameSubject = new BehaviorSubject<string | null>(this.getUserNameFromStorage());
  public currentName$ = this.currentNameSubject.asObservable();

  private currentRoleSubject = new BehaviorSubject<string | null>(this.getUserRoleFromStorage());
  public currentRole$ = this.currentRoleSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  private getUserNameFromStorage(): string | null {
    return localStorage.getItem(this.userNameKey);
  }

  private getUserRoleFromStorage(): string | null {
    return localStorage.getItem(this.userRoleKey);
  }

  // Login
  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.userRoleKey, response.role);
          localStorage.setItem(this.userNameKey, response.name);
          localStorage.setItem(this.userEmailKey, response.email);

          this.currentStateSubject.next(true);
          this.currentNameSubject.next(response.name);
          this.currentRoleSubject.next(response.role);
        }
      })
    );
  }

  // Register
  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.userRoleKey, response.role);
          localStorage.setItem(this.userNameKey, response.name);
          localStorage.setItem(this.userEmailKey, response.email);

          this.currentStateSubject.next(true);
          this.currentNameSubject.next(response.name);
          this.currentRoleSubject.next(response.role);
        }
      })
    );
  }

  // Logout
  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userRoleKey);
    localStorage.removeItem(this.userNameKey);
    localStorage.removeItem(this.userEmailKey);

    this.currentStateSubject.next(false);
    this.currentNameSubject.next(null);
    this.currentRoleSubject.next(null);

    this.router.navigate(['/login']);
  }

  // Check if logged in
  isLoggedIn(): boolean {
    return this.hasToken();
  }

  // Get user info
  getUserRole(): string | null {
    return localStorage.getItem(this.userRoleKey);
  }

  getUserName(): string | null {
    return localStorage.getItem(this.userNameKey);
  }

  getUserEmail(): string | null {
    return localStorage.getItem(this.userEmailKey);
  }

  // Forgot Password
  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email });
  }

  // Reset Password
  resetPassword(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, data);
  }

  // Social Login - Step 1: Verify Google token
  socialLogin(provider: string, token: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/social-login`, { provider, token }).pipe(
      tap(response => {
        // Only save auth state if NOT a new user (existing users get JWT)
        if (response.token && !response.newUser) {
          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.userRoleKey, response.role);
          localStorage.setItem(this.userNameKey, response.name);
          localStorage.setItem(this.userEmailKey, response.email);

          this.currentStateSubject.next(true);
          this.currentNameSubject.next(response.name);
          this.currentRoleSubject.next(response.role);
        }
      })
    );
  }

  // Social Login - Step 2: Complete registration with selected role
  completeSocialLogin(provider: string, token: string, role: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/social-login/complete`, { provider, token, role }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.userRoleKey, response.role);
          localStorage.setItem(this.userNameKey, response.name);
          localStorage.setItem(this.userEmailKey, response.email);

          this.currentStateSubject.next(true);
          this.currentNameSubject.next(response.name);
          this.currentRoleSubject.next(response.role);
        }
      })
    );
  }

  // Get Token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}
