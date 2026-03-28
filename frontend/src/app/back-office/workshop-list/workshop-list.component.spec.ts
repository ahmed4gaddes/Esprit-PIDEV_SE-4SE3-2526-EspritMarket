import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { WorkshopListComponent } from './workshop-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('WorkshopListComponent', () => {
    let component: WorkshopListComponent;
    let fixture: ComponentFixture<WorkshopListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getWorkshops', 'createWorkshop', 'updateWorkshop', 'deleteWorkshop']);
        mockService.getWorkshops.and.returnValue(of([{ id: 1, title: 'Test Workshop 1', capacity: 10, enrolledCount: 5 }]));
        mockService.deleteWorkshop.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [WorkshopListComponent, CommonModule, FormsModule, HttpClientTestingModule, RouterTestingModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(WorkshopListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the workshop list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load workshops on init', () => {
        expect(component.workshops.length).toBe(1);
        expect(component.workshops[0].title).toBe('Test Workshop 1');
        expect(mockService.getWorkshops).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteWorkshop(1);

        expect(window.confirm).toHaveBeenCalledWith('Are you sure you want to delete this workshop?');
        expect(mockService.deleteWorkshop).toHaveBeenCalledWith(1);
        expect(mockService.getWorkshops).toHaveBeenCalledTimes(2); // once on init, once on load after
    });

    it('should prepare to create new workshop', () => {
        component.openModal();
        expect(component.isEditing).toBeFalse();
        expect(component.showModal).toBeTrue();
        expect(component.currentWorkshopId).toBeNull();
    });

    it('should prepare to edit a workshop', () => {
        const w = { id: 1, title: 'Edit Me' };
        component.openModal(w);
        expect(component.isEditing).toBeTrue();
        expect(component.showModal).toBeTrue();
        expect(component.currentWorkshopId).toEqual(1);
    });
});
