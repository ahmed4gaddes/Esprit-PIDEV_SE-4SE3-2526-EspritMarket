import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-service-dashboard',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './service-dashboard.component.html',
    styleUrls: ['./service-dashboard.component.css']
})
export class ServiceDashboardComponent implements OnInit {
    stats = {
        workshops: 0,
        certificates: 0,
        internships: 0,
        courses: 0,
        registrations: 0,
        usersActive: 0
    };

    loading = true;

    constructor(private serviceModule: ServiceModuleService) { }

    ngOnInit(): void {
        this.loadStats();
    }

    loadStats() {
        this.loading = true;

        // In a real scenario you might have a single summary endpoint.
        // Here we are polling the size of each list as a placeholder for dashboard metrics.
        Promise.all([
            this.serviceModule.getWorkshops().toPromise().catch(() => []),
            this.serviceModule.getCertificates().toPromise().catch(() => []),
            this.serviceModule.getInternships().toPromise().catch(() => []),
            this.serviceModule.getCourses().toPromise().catch(() => []),
            this.serviceModule.getRegistrations().toPromise().catch(() => [])
        ]).then(([workshops, certificates, internships, courses, registrations]) => {
            this.stats.workshops = workshops ? workshops.length : 0;
            this.stats.certificates = certificates ? certificates.length : 0;
            this.stats.internships = internships ? internships.length : 0;
            this.stats.courses = courses ? courses.length : 0;
            this.stats.registrations = registrations ? registrations.length : 0;
            this.stats.usersActive = this.stats.registrations; // Rough metric for UI display

            this.loading = false;
        });
    }
}
