import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../auth.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    // Interactive Animation properties
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
        const maxTilt = 6; 

        this.tiltY = ((this.mouseX - centerX) / centerX) * maxTilt;
        this.tiltX = -((this.mouseY - centerY) / centerY) * maxTilt;
    }

    get tiltTransform() {
        return `perspective(1200px) rotateX(${this.tiltX}deg) rotateY(${this.tiltY}deg) scale3d(1.01, 1.01, 1.01)`;
    }

    // Ensemble Mascot Logic
    isPasswordFocused = false;

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

    // Form Data
    name = '';
    email = '';
    password = '';
    confirmPassword = '';
    selectedRole = '';
    dateOfBirth = '';
    phoneNumber = '';
    private readonly phoneRegex = /^\+?\d{8,15}$/;

    // UI State
    showPassword = false;
    termsAccepted = false;
    errorMessage = '';
    currentStep = 1;

    roles = [
        { value: 'CUSTOMER', label: 'Customer', icon: 'fas fa-shopping-bag', desc: 'Browse and buy amazing products.' },
        { value: 'SELLER', label: 'Seller', icon: 'fas fa-store', desc: 'Create your store and sell to students.' },
        { value: 'EXPERT', label: 'Expert', icon: 'fas fa-graduation-cap', desc: 'Share knowledge and offer courses.' },
        { value: 'COMPANY', label: 'Company', icon: 'fas fa-building', desc: 'Recruit talent and offer internships.' },
        { value: 'SPONSOR', label: 'Sponsor', icon: 'fas fa-handshake', desc: 'Support clubs and student events.' }
    ];

    // Date Validation
    maxDate: string;
    minDate: string;

    constructor(private authService: AuthService, private router: Router) {
        // Max date = today (no future dates)
        const today = new Date();
        this.maxDate = today.toISOString().split('T')[0];
        // Min date = 100 years ago
        const minYear = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate());
        this.minDate = minYear.toISOString().split('T')[0];
    }

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    selectRole(role: string) {
        this.selectedRole = role;
    }

    isValidAge(): boolean {
        if (!this.dateOfBirth) return false;
        const birth = new Date(this.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birth.getFullYear();
        const monthDiff = today.getMonth() - birth.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
            age--;
        }
        return age >= 16 && age <= 100;
    }

    nextStep() {
        this.errorMessage = '';
        if (this.currentStep === 1 && !this.selectedRole) {
            this.errorMessage = 'Please select a role to continue.';
            return;
        }
        if (this.currentStep === 2) {
            if (!this.name || !this.email || !this.dateOfBirth) {
                this.errorMessage = 'Please fill in all required fields.';
                return;
            }
            if (this.selectedRole === 'SELLER' && !this.email.endsWith('@esprit.tn')) {
                this.errorMessage = 'Sellers must use an @esprit.tn email address.';
                return;
            }
            if (!this.isValidAge()) {
                this.errorMessage = 'You must be between 16 and 100 years old to register.';
                return;
            }
        }
        this.currentStep++;
    }

    prevStep() {
        this.errorMessage = '';
        this.currentStep--;
    }

    onSubmit() {
        this.errorMessage = '';

        if (!this.termsAccepted) {
            this.errorMessage = 'You must agree to the Terms of Service and Privacy Policy.';
            return;
        }

        if (this.password.length < 6) {
            this.errorMessage = 'Password must be at least 6 characters long.';
            return;
        }

        if (this.password !== this.confirmPassword) {
            this.errorMessage = 'Passwords do not match.';
            return;
        }

        // Keep frontend phone validation aligned with backend constraint.
        if (this.phoneNumber && !this.phoneRegex.test(this.phoneNumber.trim())) {
            this.errorMessage = 'Phone number must be valid (8-15 digits).';
            return;
        }

        const userData = {
            name: this.name,
            email: this.email,
            password: this.password,
            role: this.selectedRole,
            dateOfBirth: this.dateOfBirth,
            phoneNumber: this.phoneNumber?.trim()
        };

        this.authService.register(userData).subscribe({
            next: (response) => {
                console.log('Registration successful', response);
                this.redirectByRole(response.role);
            },
            error: (err) => {
                console.error('Registration failed', err);
                const backendError = err?.error;

                if (backendError && typeof backendError === 'object') {
                    // Validation errors are returned as a map like: { phoneNumber: "..." }.
                    const firstValidationMessage = Object.values(backendError)[0] as string | undefined;
                    this.errorMessage = backendError.error || backendError.message || firstValidationMessage || 'Registration failed. Please try again.';
                    return;
                }

                this.errorMessage = 'Registration failed. Please try again.';
            }
        });
    }

    redirectByRole(role: string) {
        setTimeout(() => {
            switch (role) {
                case 'ADMIN':
                    this.router.navigate(['/admin']);
                    break;
                case 'SELLER':
                    this.router.navigate(['/seller/dashboard']);
                    break;
                case 'EXPERT':
                    // Expert UI lives under service-backoffice (same as login); /expert/dashboard is not a defined route.
                    this.router.navigate(['/service-backoffice']);
                    break;
                case 'COMPANY':
                    this.router.navigate(['/company/dashboard']);
                    break;
                case 'SPONSOR':
                    this.router.navigate(['/sponsor/dashboard']);
                    break;
                case 'CUSTOMER':
                default:
                    this.router.navigate(['/customer/dashboard']);
                    break;
            }
        }, 50);
    }
}
