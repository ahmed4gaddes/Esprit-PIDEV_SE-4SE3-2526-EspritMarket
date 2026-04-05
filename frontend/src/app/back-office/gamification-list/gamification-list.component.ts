import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-gamification-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './gamification-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class GamificationListComponent implements OnInit {
    gamifications: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            userId: [null, Validators.required],
            totalPoints: [0, Validators.required],
            currentLevel: [1, Validators.required],
            badges: ['']
        });
    }

    ngOnInit(): void { this.loadData(); }

    loadData() {
        this.loading = true;
        this.serviceModule.getGamifications().subscribe({
            next: (data) => { this.gamifications = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (Array.isArray(patchData.badges)) patchData.badges = patchData.badges.join(', ');
            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset({ totalPoints: 0, currentLevel: 1 });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = { ...this.formGroup.value };
        if (typeof val.badges === 'string' && val.badges.trim().length > 0) {
            val.badges = val.badges.split(',').map((b: string) => b.trim());
        } else {
            val.badges = [];
        }

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateGamification(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createGamification(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Delete this gamification record?')) {
            this.serviceModule.deleteGamification(id).subscribe(() => this.loadData());
        }
    }
}
