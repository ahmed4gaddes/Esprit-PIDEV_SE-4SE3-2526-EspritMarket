import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
    selector: 'app-service-layout',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './service-layout.component.html',
    styleUrls: ['./service-layout.component.css']
})
export class ServiceLayoutComponent implements OnInit {
    panelTitle = 'Service Panel';
    userName = 'User';
    userRole = 'Role';

    menuItems = [
        { icon: 'fas fa-th-large', label: 'Dashboard', path: '/service-backoffice' },
        { icon: 'fas fa-eye', label: 'Expert Live & Events', path: '/expert/dashboard/events' },
        { icon: 'fas fa-chalkboard-teacher', label: 'Workshops', path: '/service-backoffice/workshops' },
        { icon: 'fas fa-certificate', label: 'Certificates', path: '/service-backoffice/certificates' },
        { icon: 'fas fa-briefcase', label: 'Stages / Internships', path: '/service-backoffice/internships' },
        { icon: 'fas fa-book', label: 'Courses', path: '/service-backoffice/courses' },
        { icon: 'fas fa-trophy', label: 'Gamification', path: '/service-backoffice/gamification' },
        { icon: 'fas fa-check-circle', label: 'Cert. Validations', path: '/service-backoffice/cert-validations' },
        { icon: 'fas fa-file-alt', label: 'Documents', path: '/service-backoffice/documents' },
        { icon: 'fas fa-star', label: 'Evaluations', path: '/service-backoffice/evaluations' },
        { icon: 'fas fa-cogs', label: 'All Services', path: '/service-backoffice/services' },
        { icon: 'fas fa-chart-line', label: 'Statistics', path: '/service-backoffice/statistics' }
    ];

    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit() {
        this.userName = this.authService.getUserName() || 'Unknown User';

        const role = this.authService.getUserRole();
        if (role === 'EXPERT') {
            this.panelTitle = 'Expert Panel';
            this.userRole = 'Expert';
        } else if (role === 'COMPANY') {
            this.panelTitle = 'Company Panel';
            this.userRole = 'Company Partner';
        } else {
            this.userRole = role || 'Unknown';
        }
    }

    logout() {
        this.authService.logout();
    }
}
