import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-select-role',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './select-role.component.html',
    styleUrls: ['./select-role.component.css']
})
export class SelectRoleComponent {
    googleToken = '';
    userName = '';
    userEmail = '';
    userPicture = '';
    phoneNumber = '';
    dateOfBirth = '';

    selectedRole = '';
    loading = false;
    errorMessage = '';

    roles = [
        {
            value: 'CUSTOMER',
            label: 'Customer',
            icon: 'fas fa-shopping-bag',
            description: 'Browse products, shop and manage your orders',
            color: '#667eea'
        },
        {
            value: 'EXPERT',
            label: 'Expert',
            icon: 'fas fa-award',
            description: 'Review products and share your expertise',
            color: '#f093fb'
        },
        {
            value: 'COMPANY',
            label: 'Company',
            icon: 'fas fa-building',
            description: 'Manage your company and business operations',
            color: '#764ba2'
        },
        {
            value: 'SPONSOR',
            label: 'Sponsor',
            icon: 'fas fa-handshake',
            description: 'Sponsor products and promote your brand',
            color: '#e17055'
        }
    ];

    constructor(
        private authService: AuthService,
        private router: Router
    ) {
        // Get data from navigation state
        const nav = this.router.getCurrentNavigation();
        const state = nav?.extras?.state as any;

        if (state && state.googleToken) {
            this.googleToken = state.googleToken;
            this.userName = state.name || '';
            this.userEmail = state.email || '';
            this.userPicture = state.picture || '';
            this.phoneNumber = state.phoneNumber || '';
            this.dateOfBirth = state.dateOfBirth || '';
        } else {
            // No state → redirect back to login
            this.router.navigate(['/login']);
        }
    }

    selectRole(role: string) {
        this.selectedRole = role;
    }

    getSelectedLabel(): string {
        const found = this.roles.find(r => r.value === this.selectedRole);
        return found ? found.label : '...';
    }

    confirmRole() {
        if (!this.selectedRole) return;

        this.loading = true;
        this.errorMessage = '';

        this.authService.completeSocialLogin(
            'GOOGLE',
            this.googleToken,
            this.selectedRole,
            this.phoneNumber,
            this.dateOfBirth
        ).subscribe({
            next: (res) => {
                this.loading = false;
                this.redirectByRole(res.role);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = err.error?.error || 'Registration failed. Please try again.';
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
                    this.router.navigate(['/expert/dashboard']);
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
