import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StoreServiceService } from './store-service.service';

describe('StoreService', () => {
    let service: StoreServiceService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/Store';

    const fakeStore = { id: 1, name: 'Ma Boutique', description: 'Super boutique', active: true };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [StoreServiceService]
        });
        service = TestBed.inject(StoreServiceService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== GET ALL ====================
    it('should get all stores via GET', () => {
        let result: any;
        service.getAllStores().subscribe((stores: any) => result = stores);

        const req = httpMock.expectOne(`${API_URL}/getall`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeStore]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
        expect(result[0].name).toBe('Ma Boutique');
    });

    // ==================== GET BY ID ====================
    it('should get a store by ID via GET', () => {
        let result: any;
        service.getStoreById(1).subscribe((store: any) => result = store);

        const req = httpMock.expectOne(`${API_URL}/get/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeStore);

        expect(result).toBeDefined();
        expect(result.id).toBe(1);
    });

    // ==================== ADD ====================
    it('should add a store via POST', () => {
        let result: any;
        service.addStore(fakeStore as any).subscribe((store: any) => result = store);

        const req = httpMock.expectOne(`${API_URL}/addstore`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(fakeStore);
        req.flush(fakeStore);

        expect(result).toBeDefined();
        expect(result.name).toBe('Ma Boutique');
    });

    // ==================== UPDATE ====================
    it('should update a store via PUT', () => {
        const updated = { ...fakeStore, name: 'Boutique Modifiée' };
        let result: any;
        service.updateStore(updated as any, 1).subscribe((store: any) => result = store);

        const req = httpMock.expectOne(`${API_URL}/update`);
        expect(req.request.method).toBe('PUT');
        req.flush(updated);

        expect(result).toBeDefined();
        expect(result.name).toBe('Boutique Modifiée');
    });

    // ==================== DELETE ====================
    it('should delete a store via DELETE', () => {
        let deleteCompleted = false;
        service.deleteStore(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/delete/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});