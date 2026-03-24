import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServiceDashboardComponent } from './service-dashboard.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';

describe('ServiceDashboardComponent', () => {
    let component: ServiceDashboardComponent;
    let fixture: ComponentFixture<ServiceDashboardComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', [
            'getWorkshops', 'getCertificates', 'getInternships',
            'getCourses', 'getRegistrations', 'getGamifications',
            'getCalendars', 'getCertValidations', 'getSupportingDocuments',
            'getWorkshopEvaluations', 'getServices'
        ]);

        mockService.getWorkshops.and.returnValue(of([{ id: 1 }, { id: 2 }]));
        mockService.getCertificates.and.returnValue(of([{ id: 1 }]));
        mockService.getInternships.and.returnValue(of([]));
        mockService.getCourses.and.returnValue(of([{ id: 1 }, { id: 2 }, { id: 3 }]));
        mockService.getRegistrations.and.returnValue(of([{ id: 1 }]));
        mockService.getGamifications.and.returnValue(of([]));
        mockService.getCalendars.and.returnValue(of([]));
        mockService.getCertValidations.and.returnValue(of([]));
        mockService.getSupportingDocuments.and.returnValue(of([]));
        mockService.getWorkshopEvaluations.and.returnValue(of([]));
        mockService.getServices.and.returnValue(of([]));

        await TestBed.configureTestingModule({
            imports: [ServiceDashboardComponent],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(ServiceDashboardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should fetch stats properly on init', async () => {
        await fixture.whenStable();
        expect(component.stats.workshops).toBe(2);
        expect(component.stats.certificates).toBe(1);
        expect(component.stats.courses).toBe(3);

        expect(mockService.getWorkshops).toHaveBeenCalled();
        expect(mockService.getCertificates).toHaveBeenCalled();
    });
});
