import { Component, OnInit, HostListener } from '@angular/core';
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

    // --- Interactive 3D & Mascot Properties ---
    mouseX = 0;
    mouseY = 0;
    tiltX = 0;
    tiltY = 0;
    isPasswordFocused = false;

    @HostListener('document:mousemove', ['$event'])
    onMouseMove(event: MouseEvent) {
        this.mouseX = event.clientX;
        this.mouseY = event.clientY;

        const centerX = window.innerWidth / 2;
        const centerY = window.innerHeight / 2;

        const rawTiltX = (this.mouseY - centerY) / centerY;
        const rawTiltY = -(this.mouseX - centerX) / centerX;

        this.tiltX = rawTiltX * 8;
        this.tiltY = rawTiltY * 8;
    }

    get tiltTransform() {
        return `perspective(1200px) rotateX(${this.tiltX}deg) rotateY(${this.tiltY}deg) scale3d(1.01, 1.01, 1.01)`;
    }

    onPasswordFocus() { this.isPasswordFocused = true; }
    onPasswordBlur() { this.isPasswordFocused = false; }

    getPupilTransform(character: string) {
        if (this.isPasswordFocused) return 'translate(0px, 0px)';
        let maxMove = 3.5;
        if (character === 'girl') maxMove = 3;

        const moveX = (this.mouseX / window.innerWidth) * (maxMove * 2) - maxMove;
        const moveY = (this.mouseY / window.innerHeight) * (maxMove * 2) - maxMove;
        return `translate(${moveX}px, ${moveY}px)`;
    }

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
