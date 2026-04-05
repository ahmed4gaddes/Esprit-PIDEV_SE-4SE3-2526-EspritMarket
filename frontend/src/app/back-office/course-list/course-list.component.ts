import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-course-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './course-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class CourseListComponent implements OnInit {
    courses: any[] = [];
    workshops: any[] = []; // for linking
    certificates: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            durationHours: [0, Validators.required],
            mandatory: [true],
            objectives: [''],
            courseOrder: [0],
            workshopId: [null, Validators.required],
            certificateId: [null, Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getWorkshops().subscribe((w) => (this.workshops = w));
        this.serviceModule.getCertificates().subscribe((c) => (this.certificates = c || []));
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getCourses().subscribe({
            next: (data) => { this.courses = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            this.formGroup.patchValue({
                ...item,
                courseOrder: item.courseOrder ?? item.order ?? 0,
                workshopId: item.workshopId,
                certificateId: item.certificateId ?? null
            });
        } else {
            this.currentId = null;
            const maxOrder = Math.max(0, ...this.courses.map((c) => c.courseOrder ?? c.order ?? 0));
            this.formGroup.reset({
                title: '',
                description: '',
                durationHours: 0,
                mandatory: true,
                objectives: '',
                courseOrder: maxOrder + 1,
                workshopId: null,
                certificateId: null
            });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) {
            this.formGroup.markAllAsTouched();
            return;
        }
        const raw = this.formGroup.value;
        const val = {
            ...raw,
            courseOrder: raw.courseOrder,
            workshopId: raw.workshopId,
            certificateId: raw.certificateId
        };

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateCourse(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createCourse(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Are you sure you want to delete this course component?')) {
            this.serviceModule.deleteCourse(id).subscribe(() => this.loadData());
        }
    }
}
