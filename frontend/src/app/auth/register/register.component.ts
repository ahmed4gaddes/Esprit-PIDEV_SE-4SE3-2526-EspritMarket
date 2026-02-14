import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    name = '';
    email = '';
    password = '';
    confirmPassword = '';
    selectedRole = '';
    showPassword = false;

    roles = [
        { value: 'CUSTOMER', label: 'Customer', icon: 'fas fa-user', desc: 'Buy products & services' },
        { value: 'SELLER', label: 'Seller', icon: 'fas fa-store', desc: 'Sell your creations' },
        { value: 'Expert', label: 'Expert', icon: 'fas fa-graduation-cap', desc: 'Offer tutoring & courses' },
        { value: 'Company', label: 'Company', icon: 'fas fa-building', desc: 'Business account' },
        { value: 'SPONSOR', label: 'Sponsor', icon: 'fas fa-handshake', desc: 'Sponsor student projects' }
    ];

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    selectRole(role: string) {
        this.selectedRole = role;
    }

    onSubmit() {
        console.log('Register:', {
            name: this.name,
            email: this.email,
            password: this.password,
            role: this.selectedRole
        });
        // TODO: connect to backend API
    }
}
