import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
    selector: 'app-seller-layout',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './seller-layout.component.html',
    styleUrls: ['./seller-layout.component.css']
})
export class SellerLayoutComponent implements OnInit {
    panelTitle = 'Seller Dashboard';
    userName = 'User';
    userRole = 'SELLER';

    menuItems = [
        { icon: 'fas fa-store', label: 'Stores', path: '/seller/dashboard' },
        { icon: 'fas fa-box', label: 'Products', path: '/seller/dashboard/products' },
        { icon: 'fas fa-tags', label: 'Categories', path: '/seller/dashboard/categories' },
        { icon: 'fas fa-boxes', label: 'Stock', path: '/seller/dashboard/stock' },
        { icon: 'fas fa-video', label: 'Lives & Events', path: '/seller/dashboard/lives' }
    ];

    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit() {
        this.userName = this.authService.getUserName() || 'Seller User';
        this.userRole = this.authService.getUserRole() || 'SELLER';
    }

    logout() {
        this.authService.logout();
    }
}
