import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductImageService } from './product-image.service';

describe('ProductImageService', () => {
    let service: ProductImageService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/ProductImage';

    const fakeImage = { id: 1, url: 'https://esprit.tn/img/laptop.jpg', altText: 'Laptop', imageOrder: 1, productId: 1 };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ProductImageService]
        });
        service = TestBed.inject(ProductImageService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== GET ALL ====================
    it('should get all images via GET', () => {
        let result: any;
        service.getAllImages().subscribe(imgs => result = imgs);

        const req = httpMock.expectOne(`${API_URL}/getAll`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeImage]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
        expect(result[0].url).toBe('https://esprit.tn/img/laptop.jpg');
    });

    // ==================== GET BY ID ====================
    it('should get an image by ID via GET', () => {
        let result: any;
        service.getImageById(1).subscribe(img => result = img);

        const req = httpMock.expectOne(`${API_URL}/get/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeImage);

        expect(result).toBeDefined();
        expect(result.id).toBe(1);
    });

    // ==================== ADD ====================
    it('should add an image via POST', () => {
        let result: any;
        service.addImage(fakeImage as any).subscribe(img => result = img);

        const req = httpMock.expectOne(`${API_URL}/addproductimage`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(fakeImage);
        req.flush(fakeImage);

        expect(result).toBeDefined();
        expect(result.url).toBe('https://esprit.tn/img/laptop.jpg');
    });

    // ==================== UPDATE ====================
    it('should update an image via PUT', () => {
        const updated = { ...fakeImage, altText: 'Laptop Pro' };
        let result: any;
        service.updateImage(updated as any, 1).subscribe(img => result = img);

        const req = httpMock.expectOne(`${API_URL}/updateProductImage/1`);
        expect(req.request.method).toBe('PUT');
        req.flush(updated);

        expect(result).toBeDefined();
        expect(result.altText).toBe('Laptop Pro');
    });

    // ==================== DELETE ====================
    it('should delete an image via DELETE', () => {
        let deleteCompleted = false;
        service.deleteImage(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/delete/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});
