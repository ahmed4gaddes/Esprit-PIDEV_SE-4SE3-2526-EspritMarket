import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-registration-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './registration-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class RegistrationListComponent implements OnInit {
    registrations: any[] = [];
    workshops: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            userId: [null, Validators.required],
            workshopId: [null, Validators.required],
            registrationDate: [new Date().toISOString().split('T')[0], Validators.required],
            status: ['PENDING', Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getWorkshops().subscribe(w => this.workshops = w);
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getRegistrations().subscribe({
            next: (data) => {
                this.registrations = data;
                this.loading = false;
            },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (patchData.registrationDate) {
                patchData.registrationDate = new Date(patchData.registrationDate).toISOString().split('T')[0];
            }
            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset({ status: 'PENDING', registrationDate: new Date().toISOString().split('T')[0] });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateRegistration(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createRegistration(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Are you sure you want to delete this registration?')) {
            this.serviceModule.deleteRegistration(id).subscribe(() => this.loadData());
        }
    }
}
