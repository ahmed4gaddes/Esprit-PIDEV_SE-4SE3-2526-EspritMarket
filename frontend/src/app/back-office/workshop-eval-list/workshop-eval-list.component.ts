import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-workshop-eval-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './workshop-eval-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class WorkshopEvalListComponent implements OnInit {
    evaluations: any[] = [];
    workshops: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            workshopId: [null, Validators.required],
            evaluatorId: [null, Validators.required],
            rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
            feedback: [''],
            evaluationDate: [new Date().toISOString().split('T')[0], Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getWorkshops().subscribe(w => this.workshops = w);
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getWorkshopEvaluations().subscribe({
            next: (data) => { this.evaluations = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (patchData.evaluationDate) {
                patchData.evaluationDate = new Date(patchData.evaluationDate).toISOString().split('T')[0];
            }
            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset({ rating: 5, evaluationDate: new Date().toISOString().split('T')[0] });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateWorkshopEvaluation(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createWorkshopEvaluation(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Delete this evaluation/review?')) {
            this.serviceModule.deleteWorkshopEvaluation(id).subscribe(() => this.loadData());
        }
    }
}
