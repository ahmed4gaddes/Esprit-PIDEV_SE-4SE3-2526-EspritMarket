import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-forgot-password',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
    email: string = '';
    message: string = '';
    error: string = '';
    isLoading: boolean = false;

    // --- Interactive 3D & Mascot Properties ---
    mouseX = 0;
    mouseY = 0;
    tiltX = 0;
    tiltY = 0;

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

    getPupilTransform(character: string) {
        let maxMove = 3.5;
        if (character === 'girl') maxMove = 3;

        const moveX = (this.mouseX / window.innerWidth) * (maxMove * 2) - maxMove;
        const moveY = (this.mouseY / window.innerHeight) * (maxMove * 2) - maxMove;
        return `translate(${moveX}px, ${moveY}px)`;
    }

    constructor(private authService: AuthService) { }

    onSubmit() {
        if (!this.email) return;

        this.isLoading = true;
        this.message = '';
        this.error = '';

        this.authService.forgotPassword(this.email).subscribe({
            next: (response) => {
                this.message = 'Un email de réinitialisation a été envoyé ! Vérifiez votre boîte mail (et vos spams).';
                this.isLoading = false;
            },
            error: (err) => {
                this.error = 'Impossible d\'envoyer l\'email. Vérifiez que l\'adresse est correcte.';
                this.isLoading = false;
            }
        });
    }
}
