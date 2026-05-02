import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-certificate-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './certificate-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class CertificateListComponent implements OnInit {
    certificates: any[] = [];
    coursesAll: any[] = [];
    /** Cours requis pour le certificat en cours d'édition (IDs). */
    selectedCourseIds: number[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    certForm: FormGroup;
    
    uploadingImg = false;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder, private http: HttpClient) {
        this.certForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            price: [0, [Validators.required, Validators.min(0)]],
            active: [true],
            organization: ['', Validators.required],
            validUntil: [''],
            level: ['BEGINNER', Validators.required],
            status: ['PENDING'],
            documentUrl: [''],
            imageUrl: [''],
            adminComment: ['']
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getCourses().subscribe({
            next: (data) => { this.coursesAll = data || []; },
            error: () => { this.coursesAll = []; }
        });
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getCertificates().subscribe({
            next: (data) => { this.certificates = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    toggleCourseSelection(courseId: number): void {
        const idx = this.selectedCourseIds.indexOf(courseId);
        if (idx === -1) {
            this.selectedCourseIds = [...this.selectedCourseIds, courseId];
        } else {
            this.selectedCourseIds = this.selectedCourseIds.filter(id => id !== courseId);
        }
    }

    isCourseSelected(courseId: number): boolean {
        return this.selectedCourseIds.includes(courseId);
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            // Convert date string if needed for input type="date"
            const patchData = { ...item };
            if (patchData.validUntil) {
                patchData.validUntil = new Date(patchData.validUntil).toISOString().split('T')[0];
            }
            const ids = item.courseIds || (item.courses && item.courses.map((c: any) => c.id)) || [];
            this.selectedCourseIds = [...ids];
            this.certForm.patchValue(patchData);
        } else {
            this.currentId = null;
            this.selectedCourseIds = [];
            this.certForm.reset({ active: true, price: 0, level: 'BEGINNER', status: 'PENDING', imageUrl: '' });
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    onFileSelected(event: any) {
        const file: File = event.target.files[0];
        if (file) {
            this.uploadingImg = true;
            const formData = new FormData();
            formData.append('file', file);
            
            this.http.post<{ url: string, filename: string }>(`${environment.apiUrl}/api/upload`, formData).subscribe({
                next: (res) => {
                    this.certForm.patchValue({ imageUrl: res.url });
                    this.uploadingImg = false;
                },
                error: (err) => {
                    console.error('Upload failed', err);
                    this.uploadingImg = false;
                    alert('Erreur lors de l\'upload de l\'image.');
                }
            });
        }
    }

    saveData() {
        if (this.certForm.invalid) { this.certForm.markAllAsTouched(); return; }
        const val = this.certForm.value;
        val.type = 'CERTIFICATE';
        val.courseIds = this.selectedCourseIds;

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateCertificate(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createCertificate(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Are you sure you want to delete this certificate?')) {
            this.serviceModule.deleteCertificate(id).subscribe(() => this.loadData());
        }
    }
}
