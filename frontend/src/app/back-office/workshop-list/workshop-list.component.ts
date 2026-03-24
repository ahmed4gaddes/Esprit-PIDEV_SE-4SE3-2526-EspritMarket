import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-workshop-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './workshop-list.component.html',
    styleUrls: ['./workshop-list.component.css']
})
export class WorkshopListComponent implements OnInit {
    workshops: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentWorkshopId: number | null = null;
    workshopForm: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.workshopForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            price: [0, [Validators.required, Validators.min(0)]],
            active: [true],
            durationHours: [0, Validators.required],
            capacity: [0, Validators.required],
            enrolledCount: [0],
            prerequisites: [''],
            providedMaterial: [''],
            difficultyLevel: ['']
        });
    }

    ngOnInit(): void {
        this.loadWorkshops();
    }

    loadWorkshops() {
        this.loading = true;
        this.serviceModule.getWorkshops().subscribe({
            next: (data) => {
                this.workshops = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading workshops', err);
                this.loading = false;
            }
        });
    }

    openModal(workshop?: any) {
        this.isEditing = !!workshop;
        if (workshop) {
            this.currentWorkshopId = workshop.id;
            this.workshopForm.patchValue(workshop);
        } else {
            this.currentWorkshopId = null;
            this.workshopForm.reset({ active: true, price: 0, durationHours: 0, capacity: 0, enrolledCount: 0 });
        }
        this.showModal = true;
    }

    closeModal() {
        this.showModal = false;
    }

    saveWorkshop() {
        if (this.workshopForm.invalid) {
            this.workshopForm.markAllAsTouched();
            return;
        }

        const val = this.workshopForm.value;
        if (this.isEditing && this.currentWorkshopId) {
            this.serviceModule.updateWorkshop(this.currentWorkshopId, val).subscribe(() => {
                this.loadWorkshops();
                this.closeModal();
            });
        } else {
            val.type = 'WORKSHOP';
            this.serviceModule.createWorkshop(val).subscribe(() => {
                this.loadWorkshops();
                this.closeModal();
            });
        }
    }

    deleteWorkshop(id: number) {
        if (confirm('Are you sure you want to delete this workshop?')) {
            this.serviceModule.deleteWorkshop(id).subscribe(() => {
                this.loadWorkshops();
            });
        }
    }
}
