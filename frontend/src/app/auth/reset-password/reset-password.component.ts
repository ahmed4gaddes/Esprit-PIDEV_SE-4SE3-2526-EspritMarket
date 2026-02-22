import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
    selector: 'app-reset-password',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
    token: string = '';
    newPassword: string = '';
    message: string = '';
    error: string = '';
    isLoading: boolean = false;
    showPassword: boolean = false;

    hasUppercase(): boolean {
        return /[A-Z]/.test(this.newPassword);
    }

    hasNumber(): boolean {
        return /[0-9]/.test(this.newPassword);
    }

    constructor(
        private authService: AuthService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        // Capture token from URL query params
        this.route.queryParams.subscribe(params => {
            this.token = params['token'];
            if (!this.token) {
                this.error = 'Token manquant ou invalide.';
            }
        });
    }

    onSubmit() {
        if (!this.newPassword || !this.token || this.isLoading) return;

        this.isLoading = true;
        this.message = '';
        this.error = '';

        const payload = {
            token: this.token,
            newPassword: this.newPassword
        };

        this.authService.resetPassword(payload).subscribe({
            next: (response: any) => {
                this.message = response.message || 'Mot de passe réinitialisé avec succès !';
                this.isLoading = false;
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 3000);
            },
            error: (err) => {
                this.error = err.error?.error || 'Erreur lors de la réinitialisation. Le token est peut-être expiré.';
                this.isLoading = false;
            }
        });
    }
}
