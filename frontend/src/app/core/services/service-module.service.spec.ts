import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServiceModuleService } from './service-module.service';
import { AuthService } from '../../auth/auth.service';

describe('ServiceModuleService', () => {
    let service: ServiceModuleService;
    let httpMock: HttpTestingController;

    const mockAuthService = {
        getToken: jasmine.createSpy('getToken').and.returnValue('mock-token')
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                ServiceModuleService,
                { provide: AuthService, useValue: mockAuthService }
            ]
        });
        service = TestBed.inject(ServiceModuleService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should retrieve workshops with proper Authorization header', () => {
        const dummyWorkshops = [{ id: 1, title: 'Test Workshop' }];

        service.getWorkshops().subscribe(workshops => {
            expect(workshops.length).toBe(1);
            expect(workshops).toEqual(dummyWorkshops);
        });

        const req = httpMock.expectOne('http://localhost:8081/api/workshops');
        expect(req.request.method).toBe('GET');
        req.flush(dummyWorkshops);
    });

    it('should create a workshop with proper Authorization header', () => {
        const payload = { title: 'New Workshop' };

        service.createWorkshop(payload).subscribe();

        const req = httpMock.expectOne('http://localhost:8081/api/workshops');
        expect(req.request.method).toBe('POST');
        expect(req.request.headers.get('Authorization')).toEqual('Bearer mock-token');
        req.flush({ id: 2, ...payload });
    });

    it('should retrieve an internship by ID', () => {
        const dummyInternship = { id: 1, title: 'Summer Intern' };

        service.getInternshipById(1).subscribe(data => {
            expect(data).toEqual(dummyInternship);
        });

        const req = httpMock.expectOne('http://localhost:8081/api/internships/1');
        expect(req.request.method).toBe('GET');
        req.flush(dummyInternship);
    });

    it('should delete a certificate', () => {
        service.deleteCertificate(5).subscribe();

        const req = httpMock.expectOne('http://localhost:8081/api/certificates/5');
        expect(req.request.method).toBe('DELETE');
        expect(req.request.headers.get('Authorization')).toEqual('Bearer mock-token');
        req.flush(null);
    });

    it('should retrieve gamification by user ID', () => {
        service.getGamificationById(99).subscribe();

        const req = httpMock.expectOne('http://localhost:8081/api/gamifications/user/99');
        expect(req.request.method).toBe('GET');
        req.flush({});
    });
});
