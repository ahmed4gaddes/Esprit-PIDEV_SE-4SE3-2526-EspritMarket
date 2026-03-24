import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CertValidationListComponent } from './cert-validation-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('CertValidationListComponent', () => {
    let component: CertValidationListComponent;
    let fixture: ComponentFixture<CertValidationListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getCertValidations', 'createCertValidation', 'updateCertValidation', 'deleteCertValidation', 'getCertificates']);
        mockService.getCertValidations.and.returnValue(of([{ id: 1, comment: 'Looks good' }]));
        mockService.getCertificates.and.returnValue(of([]));
        mockService.deleteCertValidation.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [CertValidationListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CertValidationListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the cert validation list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load validations on init', () => {
        expect(component.validations.length).toBe(1);
        expect(mockService.getCertValidations).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
    });
});
