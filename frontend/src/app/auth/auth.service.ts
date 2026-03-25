import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private userRoleKey = 'user_role';
  private userNameKey = 'user_name';
  private userEmailKey = 'user_email';

  private currentStateSubject = new BehaviorSubject<boolean>(this.isLoggedInCheck());
  public isLoggedIn$ = this.currentStateSubject.asObservable();

  private currentNameSubject = new BehaviorSubject<string | null>(this.getUserNameFromStorage());
  public currentName$ = this.currentNameSubject.asObservable();

  private currentRoleSubject = new BehaviorSubject<string | null>(this.getUserRoleFromStorage());
  public currentRole$ = this.currentRoleSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  private isLoggedInCheck(): boolean {
    // We check role presence as an indicator of login state
    // (the actual JWT is in HttpOnly cookie, invisible to JS)
    return !!localStorage.getItem(this.userRoleKey);
  }

  private getUserNameFromStorage(): string | null {
    return localStorage.getItem(this.userNameKey);
  }

  private getUserRoleFromStorage(): string | null {
    return localStorage.getItem(this.userRoleKey);
  }

  // Login
  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials, { withCredentials: true }).pipe(
      tap(response => {
        // Token is now in HttpOnly cookie (set by server), not in body
        localStorage.setItem(this.userRoleKey, response.role);
        localStorage.setItem(this.userNameKey, response.name);
        localStorage.setItem(this.userEmailKey, response.email);

        this.currentStateSubject.next(true);
        this.currentNameSubject.next(response.name);
        this.currentRoleSubject.next(response.role);
      })
    );
  }

  // Register
  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData, { withCredentials: true }).pipe(
      tap(response => {
        localStorage.setItem(this.userRoleKey, response.role);
        localStorage.setItem(this.userNameKey, response.name);
        localStorage.setItem(this.userEmailKey, response.email);

        this.currentStateSubject.next(true);
        this.currentNameSubject.next(response.name);
        this.currentRoleSubject.next(response.role);
      })
    );
  }

  // Logout
  logout(): void {
    // Call backend to clear the HttpOnly cookie
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe();

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
    return this.isLoggedInCheck();
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
    return this.http.post<any>(`${this.apiUrl}/social-login`, { provider, token }, { withCredentials: true }).pipe(
      tap(response => {
        // Only save auth state if NOT a new user (existing users get JWT in cookie)
        if (!response.newUser && response.role) {
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

  // Social Login - Step 2: Complete registration with selected role and additional info
  completeSocialLogin(provider: string, token: string, role: string, phoneNumber?: string, dateOfBirth?: string | Date): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/social-login/complete`, { provider, token, role, phoneNumber, dateOfBirth }, { withCredentials: true }).pipe(
      tap(response => {
        if (response.role) {
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

  // getToken is no longer needed (cookie is sent automatically)
  // Kept for backward compatibility but returns null
  getToken(): string | null {
    return null;
  }
}

