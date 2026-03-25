import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
    let service: ProductService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/Product';

    const fakeProduct = {
        id: 1, name: 'Laptop ESPRIT', description: 'PC portable',
        price: 1500, stock: 10, active: true, storeId: 1, categoryId: 1
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ProductService]
        });
        service = TestBed.inject(ProductService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== GET ALL ====================
    it('should get all products via GET', () => {
        let result: any;
        service.getAllProducts().subscribe(products => result = products);

        const req = httpMock.expectOne(`${API_URL}/getall`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeProduct]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
        expect(result[0].name).toBe('Laptop ESPRIT');
    });

    // ==================== GET BY ID ====================
    it('should get a product by ID via GET', () => {
        let result: any;
        service.getProductById(1).subscribe(product => result = product);

        const req = httpMock.expectOne(`${API_URL}/get/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeProduct);

        expect(result).toBeDefined();
        expect(result.id).toBe(1);
    });

    // ==================== ADD ====================
    it('should add a product via POST', () => {
        let result: any;
        service.addProduct(fakeProduct as any).subscribe(product => result = product);

        const req = httpMock.expectOne(`${API_URL}/addprodcut`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(fakeProduct);
        req.flush(fakeProduct);

        expect(result).toBeDefined();
        expect(result.name).toBe('Laptop ESPRIT');
    });

    // ==================== UPDATE ====================
    it('should update a product via PUT', () => {
        const updated = { ...fakeProduct, name: 'Laptop Pro' };
        let result: any;
        service.updateProduct(updated as any, 1).subscribe(product => result = product);

        const req = httpMock.expectOne(`${API_URL}/update/1`);
        expect(req.request.method).toBe('PUT');
        req.flush(updated);

        expect(result).toBeDefined();
        expect(result.name).toBe('Laptop Pro');
    });

    // ==================== DELETE ====================
    it('should delete a product via DELETE', () => {
        let deleteCompleted = false;
        service.deleteProduct(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/delete/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});
