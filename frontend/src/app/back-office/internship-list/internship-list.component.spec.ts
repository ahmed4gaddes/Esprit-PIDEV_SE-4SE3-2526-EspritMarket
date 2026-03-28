import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InternshipListComponent } from './internship-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('InternshipListComponent', () => {
    let component: InternshipListComponent;
    let fixture: ComponentFixture<InternshipListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', [
            'getInternships',
            'createInternship',
            'updateInternship',
            'deleteInternship',
            'getInternshipApplicationsByInternship'
        ]);
        mockService.getInternships.and.returnValue(of([{ id: 1, title: 'Summer Intern' }]));
        mockService.getInternshipApplicationsByInternship.and.returnValue(of([]));
        mockService.deleteInternship.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [InternshipListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(InternshipListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the internship list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load internships on init', () => {
        expect(component.internships.length).toBe(1);
        expect(mockService.getInternships).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteData(1);
        expect(mockService.deleteInternship).toHaveBeenCalledWith(1);
    });
});
