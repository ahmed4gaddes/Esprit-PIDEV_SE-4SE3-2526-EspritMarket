import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UploadService } from './upload.service';

describe('UploadService', () => {
    let service: UploadService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/api/upload';

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [UploadService]
        });
        service = TestBed.inject(UploadService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        // Vérifier qu'aucune requête HTTP non attendue n'a été faite
        httpMock.verify();
    });

    // ==================== uploadImage ====================
    it('should upload a file and return the URL', () => {
        // ARRANGE
        const fakeFile = new File(['contenu image'], 'photo.jpg', { type: 'image/jpeg' });
        const fakeResponse = { url: 'http://localhost:8081/uploads/photo.jpg', filename: 'photo.jpg' };
        let resultUrl: string | undefined;

        // ACT
        service.uploadImage(fakeFile).subscribe(url => resultUrl = url);

        // Intercepter la requête HTTP et simuler la réponse du backend
        const req = httpMock.expectOne(API_URL);
        expect(req.request.method).toBe('POST');  // Vérifier que c'est bien un POST

        req.flush(fakeResponse);  // Simuler la réponse du serveur

        // ASSERT
        expect(resultUrl).toBe('http://localhost:8081/uploads/photo.jpg');
    });

    it('should include the file in FormData', () => {
        // ARRANGE
        const fakeFile = new File(['data'], 'image.png', { type: 'image/png' });

        // ACT
        service.uploadImage(fakeFile).subscribe();

        // Intercepter la requête et vérifier le contenu
        const req = httpMock.expectOne(API_URL);
        expect(req.request.body instanceof FormData).toBeTrue(); // Vérifier que c'est bien du FormData

        req.flush({ url: 'http://localhost:8081/uploads/image.png', filename: 'image.png' });
    });

    it('should handle upload error gracefully', () => {
        // ARRANGE
        const fakeFile = new File(['data'], 'image.jpg');
        let errorReceived = false;

        // ACT
        service.uploadImage(fakeFile).subscribe({
            next: () => { },
            error: () => errorReceived = true
        });

        // Simuler une erreur serveur HTTP 500
        const req = httpMock.expectOne(API_URL);
        req.flush('Internal Server Error', { status: 500, statusText: 'Server Error' });

        // ASSERT
        expect(errorReceived).toBeTrue();
    });
});
