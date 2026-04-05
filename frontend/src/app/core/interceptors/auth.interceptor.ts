import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

const apiPathNeedsCredentials = (url: string): boolean => {
    if (url.includes('localhost:8081')) {
        return true;
    }
    if (!url.startsWith('/') || url.startsWith('//')) {
        return false;
    }
    return /^\/(api|Store|ProductImage|Product|category|Stock)(\/|$)/.test(url);
};

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);

    // Add withCredentials to all API requests so the HttpOnly JWT cookie is sent automatically
    if (apiPathNeedsCredentials(req.url)) {
        const clonedReq = req.clone({
            withCredentials: true
        });
        return next(clonedReq).pipe(
            catchError((error) => {
                const isAuthApi = clonedReq.url.includes('/api/auth/');
                if (error?.status === 401 && !isAuthApi) {
                    localStorage.removeItem('user_role');
                    localStorage.removeItem('user_name');
                    localStorage.removeItem('user_email');
                    localStorage.removeItem('token');
                    router.navigate(['/login'], { queryParams: { reason: 'session_expired' } });
                }
                return throwError(() => error);
            })
        );
    }

    return next(req);
};

