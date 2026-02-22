import { Component } from '@angular/core';
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
