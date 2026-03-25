import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    // Add withCredentials to all API requests so the HttpOnly JWT cookie is sent automatically
    if (req.url.includes('localhost:8081')) {
        const clonedReq = req.clone({
            withCredentials: true
        });
        return next(clonedReq);
    }

    return next(req);
};

