import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-internship-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './internship-list.component.html',
    styleUrls: ['./internship-list.component.css', '../workshop-list/workshop-list.component.css']
})
export class InternshipListComponent implements OnInit {
    internships: any[] = [];
    applicationCountByInternshipId: Record<number, number> = {};
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;
    selectedInternshipForApplications: any | null = null;
    applicationsModalOpen = false;
    applicationsLoading = false;
    applications: any[] = [];
    decisionModalOpen = false;
    decisionStatus: 'ACCEPTED' | 'REJECTED' = 'ACCEPTED';
    decisionApplicationId: number | null = null;
    decisionNote = '';

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            active: [true],
            company: ['', Validators.required],
            durationMonths: [0, Validators.required],
            companyAddress: [''],
            companyTutorName: [''],
            companyTutorEmail: ['', Validators.email],
            academicTutorName: [''],
            agreementSigned: [false],
            startDate: [''],
            endDate: ['']
        });
    }

    ngOnInit(): void { this.loadData(); }

    loadData() {
        this.loading = true;
        this.serviceModule.getInternships().subscribe({
            next: (data) => {
                this.internships = data;
                this.loading = false;
                this.loadApplicationCounts();
            },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    private loadApplicationCounts(): void {
        this.applicationCountByInternshipId = {};
        for (const internship of this.internships) {
            const internshipId = internship?.id;
            if (!internshipId) continue;
            this.serviceModule.getInternshipApplicationsByInternship(internshipId).subscribe({
                next: (apps) => this.applicationCountByInternshipId[internshipId] = (apps || []).length,
                error: () => this.applicationCountByInternshipId[internshipId] = 0
            });
        }
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (patchData.startDate) patchData.startDate = new Date(patchData.startDate).toISOString().split('T')[0];
            if (patchData.endDate) patchData.endDate = new Date(patchData.endDate).toISOString().split('T')[0];
            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset({ active: true, durationMonths: 0, agreementSigned: false });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = { ...this.formGroup.value, price: 0, type: 'INTERNSHIP' as const };

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateInternship(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createInternship(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Are you sure you want to delete this internship?')) {
            this.serviceModule.deleteInternship(id).subscribe({
                next: () => this.loadData(),
                error: (err) => {
                    console.error(err);
                    alert('Delete failed: ' + (err?.error?.error || err?.error?.message || 'Unknown server error'));
                }
            });
        }
    }

    openApplications(internship: any): void {
        this.selectedInternshipForApplications = internship;
        this.applicationsModalOpen = true;
        this.loadApplications(internship.id);
    }

    closeApplicationsModal(): void {
        this.applicationsModalOpen = false;
        this.selectedInternshipForApplications = null;
        this.applications = [];
    }

    loadApplications(internshipId: number): void {
        this.applicationsLoading = true;
        this.serviceModule.getInternshipApplicationsByInternship(internshipId).subscribe({
            next: (data) => {
                this.applications = data || [];
                this.applicationsLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.applications = [];
                this.applicationsLoading = false;
            }
        });
    }

    openDecisionModal(applicationId: number, status: 'ACCEPTED' | 'REJECTED'): void {
        this.decisionApplicationId = applicationId;
        this.decisionStatus = status;
        this.decisionNote = '';
        this.decisionModalOpen = true;
    }

    closeDecisionModal(): void {
        this.decisionModalOpen = false;
        this.decisionApplicationId = null;
        this.decisionNote = '';
    }

    confirmDecision(): void {
        if (!this.decisionApplicationId) return;
        this.serviceModule.decideInternshipApplication(
            this.decisionApplicationId,
            this.decisionStatus,
            this.decisionNote
        ).subscribe({
            next: () => {
                this.closeDecisionModal();
                if (this.selectedInternshipForApplications?.id) {
                    this.loadApplications(this.selectedInternshipForApplications.id);
                }
                this.loadData();
            },
            error: (err) => {
                console.error(err);
                alert('Failed to update application: ' + (err?.error?.error || err?.error?.message || 'Unknown error'));
            }
        });
    }
}
