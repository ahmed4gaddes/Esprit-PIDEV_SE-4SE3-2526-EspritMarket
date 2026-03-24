import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SupportingDocListComponent } from './supporting-doc-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('SupportingDocListComponent', () => {
    let component: SupportingDocListComponent;
    let fixture: ComponentFixture<SupportingDocListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getSupportingDocuments', 'createSupportingDocument', 'updateSupportingDocument', 'deleteSupportingDocument', 'getCertificates', 'getServices']);
        mockService.getSupportingDocuments.and.returnValue(of([{ id: 1, name: 'Transcript.pdf' }]));
        mockService.getCertificates.and.returnValue(of([]));
        mockService.getServices.and.returnValue(of([]));
        mockService.deleteSupportingDocument.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [SupportingDocListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SupportingDocListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the supporting doc list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load documents on init', () => {
        expect(component.documents.length).toBe(1);
        expect(mockService.getSupportingDocuments).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteData(1);
        expect(mockService.deleteSupportingDocument).toHaveBeenCalledWith(1);
    });
});
