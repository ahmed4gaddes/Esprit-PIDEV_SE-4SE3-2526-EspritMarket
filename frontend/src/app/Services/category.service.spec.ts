import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CategoryService } from './category.service';

describe('CategoryService', () => {
    let service: CategoryService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/category';

    const fakeCategory = { id: 1, name: 'Électronique', description: 'Appareils', type: 'ELECTRONICS' };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [CategoryService]
        });
        service = TestBed.inject(CategoryService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== GET ALL ====================
    it('should get all categories via GET', () => {
        let result: any;
        service.getAllCategories().subscribe(cats => result = cats);

        const req = httpMock.expectOne(`${API_URL}/getall`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeCategory]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
        expect(result[0].name).toBe('Électronique');
    });

    // ==================== GET BY STORE ====================
    it('should get categories by store ID via GET', () => {
        let result: any;
        service.getCategoriesByStore(1).subscribe(cats => result = cats);

        const req = httpMock.expectOne(`${API_URL}/by-store/1`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeCategory]);

        expect(result).toBeDefined();
        expect(result.length).toBe(1);
    });

    // ==================== GET BY ID ====================
    it('should get a category by ID via GET', () => {
        let result: any;
        service.getCategoryById(1).subscribe(cat => result = cat);

        const req = httpMock.expectOne(`${API_URL}/get/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeCategory);

        expect(result).toBeDefined();
        expect(result.id).toBe(1);
    });

    // ==================== ADD ====================
    it('should add a category via POST', () => {
        let result: any;
        service.addCategory(fakeCategory as any).subscribe(cat => result = cat);

        const req = httpMock.expectOne(`${API_URL}/addcategory`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(fakeCategory);
        req.flush(fakeCategory);

        expect(result).toBeDefined();
        expect(result.name).toBe('Électronique');
    });

    // ==================== UPDATE ====================
    it('should update a category via PUT', () => {
        const updated = { ...fakeCategory, name: 'High-Tech' };
        let result: any;
        service.updateCategory(updated as any, 1).subscribe(cat => result = cat);

        const req = httpMock.expectOne(`${API_URL}/updateCategory/1`);
        expect(req.request.method).toBe('PUT');
        req.flush(updated);

        expect(result).toBeDefined();
        expect(result.name).toBe('High-Tech');
    });

    // ==================== DELETE ====================
    it('should delete a category via DELETE', () => {
        let deleteCompleted = false;
        service.deleteCategory(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/delete/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});
