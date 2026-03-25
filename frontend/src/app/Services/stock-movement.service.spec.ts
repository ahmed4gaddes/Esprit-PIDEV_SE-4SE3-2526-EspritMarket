import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StockMovementService } from './stock-movement.service';

describe('StockMovementService', () => {
    let service: StockMovementService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/Stock';

    const fakeMovement = { id: 1, quantity: 50, type: 'IN', date: new Date(), productId: 1 };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [StockMovementService]
        });
        service = TestBed.inject(StockMovementService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== GET ALL ====================
    it('should get all movements via GET', () => {
        let result: any;
        service.getAllMovements().subscribe(movs => result = movs);

        const req = httpMock.expectOne(`${API_URL}/getall`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeMovement]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
        expect(result[0].quantity).toBe(50);
    });

    // ==================== GET BY ID ====================
    it('should get a movement by ID via GET', () => {
        let result: any;
        service.getMovementById(1).subscribe(mov => result = mov);

        const req = httpMock.expectOne(`${API_URL}/get/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeMovement);

        expect(result).toBeDefined();
        expect(result.id).toBe(1);
    });

    // ==================== ADD ====================
    it('should add a movement via POST', () => {
        let result: any;
        service.addMovement(fakeMovement as any).subscribe(mov => result = mov);

        const req = httpMock.expectOne(`${API_URL}/addstock`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(fakeMovement);
        req.flush(fakeMovement);

        expect(result).toBeDefined();
        expect(result.quantity).toBe(50);
    });

    // ==================== UPDATE ====================
    it('should update a movement via PUT', () => {
        const updated = { ...fakeMovement, quantity: 100 };
        let result: any;
        service.updateMovement(updated as any, 1).subscribe(mov => result = mov);

        const req = httpMock.expectOne(`${API_URL}/update/1`);
        expect(req.request.method).toBe('PUT');
        req.flush(updated);

        expect(result).toBeDefined();
        expect(result.quantity).toBe(100);
    });

    // ==================== DELETE ====================
    it('should delete a movement via DELETE', () => {
        let deleteCompleted = false;
        service.deleteMovement(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/delete/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});
