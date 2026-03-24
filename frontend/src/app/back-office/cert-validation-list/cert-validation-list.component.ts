import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-cert-validation-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './cert-validation-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class CertValidationListComponent implements OnInit {
    validations: any[] = [];
    certificates: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            certificateId: [null, Validators.required],
            validatorId: [null, Validators.required],
            validationDate: [new Date().toISOString().split('T')[0], Validators.required],
            valid: [false],
            comments: ['']
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getCertificates().subscribe(c => this.certificates = c);
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getCertValidations().subscribe({
            next: (data) => { this.validations = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (patchData.validationDate) {
                patchData.validationDate = new Date(patchData.validationDate).toISOString().split('T')[0];
            }
            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset({ valid: false, validationDate: new Date().toISOString().split('T')[0] });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateCertValidation(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createCertValidation(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Delete this certificate validation?')) {
            this.serviceModule.deleteCertValidation(id).subscribe(() => this.loadData());
        }
    }
}
