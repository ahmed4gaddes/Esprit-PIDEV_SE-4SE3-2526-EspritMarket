import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceModuleService } from '../../core/services/service-module.service';

@Component({
    selector: 'app-calendar-list',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './calendar-list.component.html',
    styleUrls: ['../workshop-list/workshop-list.component.css']
})
export class CalendarListComponent implements OnInit {
    events: any[] = [];
    servicesList: any[] = [];
    loading = true;
    showModal = false;
    isEditing = false;
    currentId: number | null = null;
    formGroup: FormGroup;

    constructor(private serviceModule: ServiceModuleService, private fb: FormBuilder) {
        this.formGroup = this.fb.group({
            serviceId: [null, Validators.required],
            eventDate: ['', Validators.required],
            startTime: ['', Validators.required],
            endTime: ['', Validators.required],
            meetingLink: [''],
            location: [''],
            notes: ['']
        });
    }

    ngOnInit(): void {
        this.loadData();
        this.serviceModule.getServices().subscribe(s => this.servicesList = s);
    }

    loadData() {
        this.loading = true;
        this.serviceModule.getCalendars().subscribe({
            next: (data) => { this.events = data; this.loading = false; },
            error: (err) => { console.error(err); this.loading = false; }
        });
    }

    openModal(item?: any) {
        this.isEditing = !!item;
        if (item) {
            this.currentId = item.id;
            const patchData = { ...item };
            if (patchData.eventDate) patchData.eventDate = new Date(patchData.eventDate).toISOString().split('T')[0];

            // Attempt to extract time from strings HH:mm
            if (patchData.startTime && patchData.startTime.length >= 5) patchData.startTime = patchData.startTime.substring(0, 5);
            if (patchData.endTime && patchData.endTime.length >= 5) patchData.endTime = patchData.endTime.substring(0, 5);

            this.formGroup.patchValue(patchData);
        } else {
            this.currentId = null;
            this.formGroup.reset();
        }
        this.showModal = true;
    }

    closeModal() { this.showModal = false; }

    saveData() {
        if (this.formGroup.invalid) { this.formGroup.markAllAsTouched(); return; }
        const val = this.formGroup.value;

        // Spring expects LocalTime formats.
        if (val.startTime.length === 5) val.startTime = val.startTime + ":00";
        if (val.endTime.length === 5) val.endTime = val.endTime + ":00";

        if (this.isEditing && this.currentId) {
            this.serviceModule.updateCalendar(this.currentId, val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        } else {
            this.serviceModule.createCalendar(val).subscribe(() => {
                this.loadData(); this.closeModal();
            });
        }
    }

    deleteData(id: number) {
        if (confirm('Delete this calendar event?')) {
            this.serviceModule.deleteCalendar(id).subscribe(() => this.loadData());
        }
    }
}
