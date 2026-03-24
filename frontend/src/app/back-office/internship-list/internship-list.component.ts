import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-internship-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './internship-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class InternshipListComponent implements OnInit {
    internships: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            price: [0, [Validators.required, Validators.min(0)]],
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
            next: (data) => { this.internships = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
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
            this.formGroup.reset({ active: true, price: 0, durationMonths: 0, agreementSigned: false });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;
        val.type = 'INTERNSHIP';

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
            this.serviceModule.deleteInternship(id).subscribe(() => this.loadData());
        }
    }
}
