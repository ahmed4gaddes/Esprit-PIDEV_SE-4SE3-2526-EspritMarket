import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkshopEvalListComponent } from './workshop-eval-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('WorkshopEvalListComponent', () => {
    let component: WorkshopEvalListComponent;
    let fixture: ComponentFixture<WorkshopEvalListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getWorkshopEvaluations', 'createWorkshopEvaluation', 'updateWorkshopEvaluation', 'deleteWorkshopEvaluation', 'getRegistrations', 'getWorkshops']);
        mockService.getWorkshopEvaluations.and.returnValue(of([{ id: 1, rating: 5, comments: 'Great' }]));
        mockService.getRegistrations.and.returnValue(of([]));
        mockService.getWorkshops.and.returnValue(of([]));
        mockService.deleteWorkshopEvaluation.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [WorkshopEvalListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(WorkshopEvalListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the workshop evaluation list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load evaluations on init', () => {
        expect(component.evaluations.length).toBe(1);
        expect(mockService.getWorkshopEvaluations).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteData(1);
        expect(mockService.deleteWorkshopEvaluation).toHaveBeenCalledWith(1);
    });
});
