import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const expectedRoles = route.data['roles'] as Array<string>;

    if (!authService.isLoggedIn()) {
        router.navigate(['/login']);
        return false;
    }

    const userRole = authService.getUserRole();
    if (userRole && expectedRoles.includes(userRole)) {
        return true;
    } else {
        // Redirect to home or unauthorized page
        console.warn('Unauthorized access attempt to ' + state.url);
        router.navigate(['/']);
        return false;
    }
};
