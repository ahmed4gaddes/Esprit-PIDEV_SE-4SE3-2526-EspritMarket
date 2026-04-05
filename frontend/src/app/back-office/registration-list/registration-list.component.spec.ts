import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegistrationListComponent } from './registration-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('RegistrationListComponent', () => {
    let component: RegistrationListComponent;
    let fixture: ComponentFixture<RegistrationListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getRegistrations', 'createRegistration', 'updateRegistration', 'deleteRegistration', 'getWorkshops']);
        mockService.getRegistrations.and.returnValue(of([{ id: 1, status: 'REGISTERED' }]));
        mockService.getWorkshops.and.returnValue(of([]));
        mockService.deleteRegistration.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [RegistrationListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(RegistrationListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the registration list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load registrations on init', () => {
        expect(component.registrations.length).toBe(1);
        expect(mockService.getRegistrations).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
    });
});
