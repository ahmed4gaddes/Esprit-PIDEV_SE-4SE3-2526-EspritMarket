import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';  // ✅ bon chemin

import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
//import { AuthService } from '../Services/auth.service';
@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService {

 constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.authService.getToken();

    if (token) {
      // ✅ Ajouter token dans chaque requête
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    return next.handle(req);
  }}
