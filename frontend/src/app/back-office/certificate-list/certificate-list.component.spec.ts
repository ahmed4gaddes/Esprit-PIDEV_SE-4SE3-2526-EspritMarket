import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CertificateListComponent } from './certificate-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('CertificateListComponent', () => {
    let component: CertificateListComponent;
    let fixture: ComponentFixture<CertificateListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', [
            'getCertificates',
            'getCourses',
            'createCertificate',
            'updateCertificate',
            'deleteCertificate'
        ]);
        mockService.getCertificates.and.returnValue(of([{ id: 1, title: 'Sample Certificate' }]));
        mockService.getCourses.and.returnValue(of([]));
        mockService.deleteCertificate.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [CertificateListComponent, CommonModule, FormsModule, HttpClientTestingModule, RouterTestingModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CertificateListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the certificate list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load certificates on init', () => {
        expect(component.certificates.length).toBe(1);
        expect(mockService.getCertificates).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteData(1);
        expect(mockService.deleteCertificate).toHaveBeenCalledWith(1);
    });
});
