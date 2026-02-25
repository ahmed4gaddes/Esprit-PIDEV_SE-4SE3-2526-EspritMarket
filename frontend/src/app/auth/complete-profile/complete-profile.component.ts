import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
    selector: 'app-complete-profile',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './complete-profile.component.html',
    styleUrls: ['./complete-profile.component.css']
})
export class CompleteProfileComponent implements OnInit {
    googleToken = '';
    userName = '';
    userEmail = '';
    userPicture = '';

    phoneNumber = '';
    dateOfBirth = '';

    errorMessage = '';

    constructor(private router: Router) {
        // Get data from navigation state (passed from Google Login)
        const nav = this.router.getCurrentNavigation();
        const state = nav?.extras?.state as any;

        if (state && state.googleToken) {
            this.googleToken = state.googleToken;
            this.userName = state.name || '';
            this.userEmail = state.email || '';
            this.userPicture = state.picture || '';
        } else {
            // No state → redirect back to login
            this.router.navigate(['/login']);
        }
    }

    ngOnInit(): void {
    }

    onSubmit() {
        this.errorMessage = '';

        if (!this.phoneNumber) {
            this.errorMessage = 'Le numéro de téléphone est obligatoire.';
            return;
        }

        if (!this.dateOfBirth) {
            this.errorMessage = 'La date de naissance est obligatoire.';
            return;
        }

        // Checking age (16+)
        const dob = new Date(this.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - dob.getFullYear();
        const m = today.getMonth() - dob.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
            age--;
        }

        if (age < 16) {
            this.errorMessage = 'Vous devez avoir au moins 16 ans pour vous inscrire.';
            return;
        }

        // Navigate to role selection and pass all the collected data
        this.router.navigate(['/select-role'], {
            state: {
                googleToken: this.googleToken,
                name: this.userName,
                email: this.userEmail,
                picture: this.userPicture,
                phoneNumber: this.phoneNumber,
                dateOfBirth: this.dateOfBirth
            }
        });
    }
}
