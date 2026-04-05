import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-service-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './service-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class ServiceListComponent implements OnInit {
    services: any[] = [];
    loading = true;
    showModal = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            price: [0, [Validators.required, Validators.min(0)]],
            active: [true]
        });
    }

    ngOnInit(): void { this.loadData(); }

    loadData() {
        this.loading = true;
        this.serviceModule.getServices().subscribe({
            next: (data) => { this.services = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item: any) {
        this.currentId = item.id;
        this.formGroup.patchValue(item);
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid || !this.currentId) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;

        // Abstract base class, so we only update.
        this.serviceModule.updateService(this.currentId, val).subscribe(() => {
            this.loadData(); this.closeModal();
        });
    }

    deleteData(id: number) {
        if (confirm('Delete this service globally?')) {
            this.serviceModule.deleteService(id).subscribe(() => this.loadData());
        }
    }
}
