import { Component, OnInit, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

declare var google: any;

const GOOGLE_CLIENT_ID = '657824293337-88tekq3h4p524uo1npselrhlv72k8v7a.apps.googleusercontent.com';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    email = '';
    password = '';
    showPassword = false;
    errorMessage = '';
    socialLoading = false;

    constructor(
        private authService: AuthService,
        private router: Router,
        private ngZone: NgZone
    ) { }

    ngOnInit() {
        this.initGoogleSignIn();
    }

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    // ========== GOOGLE SIGN-IN ==========
    initGoogleSignIn() {
        if (typeof google === 'undefined') {
            const script = document.createElement('script');
            script.src = 'https://accounts.google.com/gsi/client';
            script.async = true;
            script.defer = true;
            script.onload = () => this.renderGoogleButton();
            document.head.appendChild(script);
        } else {
            this.renderGoogleButton();
        }
    }

    renderGoogleButton() {
        setTimeout(() => {
            if (typeof google !== 'undefined' && google.accounts) {
                google.accounts.id.initialize({
                    client_id: GOOGLE_CLIENT_ID,
                    callback: (response: any) => this.handleGoogleCallback(response)
                });

                const googleBtnContainer = document.getElementById('google-signin-btn');
                if (googleBtnContainer) {
                    google.accounts.id.renderButton(googleBtnContainer, {
                        theme: 'outline',
                        size: 'large',
                        width: '100%',
                        text: 'continue_with',
                        shape: 'pill'
                    });
                }
            }
        }, 100);
    }

    handleGoogleCallback(response: any) {
        this.ngZone.run(() => {
            this.socialLoading = true;
            this.errorMessage = '';

            this.authService.socialLogin('GOOGLE', response.credential).subscribe({
                next: (res) => {
                    this.socialLoading = false;
                    if (res.newUser) {
                        // New user → navigate to role selection with token data
                        this.router.navigate(['/select-role'], {
                            state: {
                                googleToken: response.credential,
                                name: res.name,
                                email: res.email,
                                picture: res.picture
                            }
                        });
                    } else {
                        // Existing user → redirect to dashboard
                        this.redirectByRole(res.role);
                    }
                },
                error: (err) => {
                    this.socialLoading = false;
                    this.errorMessage = err.error?.error || 'Google login failed.';
                }
            });
        });
    }

    // ========== STANDARD LOGIN ==========
    onSubmit() {
        this.errorMessage = '';
        this.authService.login({ email: this.email, password: this.password }).subscribe({
            next: (response) => {
                this.redirectByRole(response.role);
            },
            error: (err) => {
                console.error('Login failed', err);
                this.errorMessage = 'Email ou mot de passe incorrect.';
            }
        });
    }

    // ========== REDIRECT BY ROLE ==========
    redirectByRole(role: string) {
        // Small delay to ensure localStorage is fully committed before roleGuard checks it
        setTimeout(() => {
            switch (role) {
                case 'ADMIN':
                    this.router.navigate(['/admin']);
                    break;
                case 'SELLER':
                    this.router.navigate(['/seller/dashboard']);
                    break;
                case 'SPONSOR':
                    this.router.navigate(['/sponsor/dashboard']);
                    break;
                case 'EXPERT':
                case 'COMPANY':
                    this.router.navigate(['/service-backoffice']);
                    break;
                case 'CUSTOMER':
                default:
                    this.router.navigate(['/']);
                    break;
            }
        }, 50);
    }
}
