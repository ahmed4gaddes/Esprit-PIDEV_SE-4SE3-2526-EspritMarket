import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductAssessmentService } from './product-assessment.service';
import { ProductAssessment } from '../models/product-assessment';

describe('ProductAssessmentService', () => {
  let service: ProductAssessmentService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/assessments';

  const assessment: ProductAssessment = { star: 4, comment: 'ok' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductAssessmentService]
    });
    service = TestBed.inject(ProductAssessmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('addAssessment should POST with productId and withCredentials', () => {
    service.addAssessment(100, assessment).subscribe();
    const req = httpMock.expectOne((r) => r.url === base && r.params.get('productId') === '100');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ ...assessment, id: 1 });
  });

  it('getByProduct should GET list', () => {
    let out: ProductAssessment[] | undefined;
    service.getByProduct(5).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/product/5`);
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('getAverageByProduct should GET average DTO', () => {
    let out: { average: number; count: number } | undefined;
    service.getAverageByProduct(5).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/product/5/average`);
    req.flush({ average: 3.5, count: 10 });
    expect(out).toEqual({ average: 3.5, count: 10 });
  });

  it('getByUser should GET user assessments', () => {
    let out: ProductAssessment[] | undefined;
    service.getByUser(2).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/user/2`);
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('getById should GET one', () => {
    let out: ProductAssessment | undefined;
    service.getById(9).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/9`);
    req.flush(assessment);
    expect(out?.star).toBe(4);
  });

  it('update should PUT', () => {
    service.update(9, assessment).subscribe();
    const req = httpMock.expectOne(`${base}/9`);
    expect(req.request.method).toBe('PUT');
    req.flush(assessment);
  });

  it('delete should DELETE', () => {
    service.delete(9).subscribe();
    const req = httpMock.expectOne(`${base}/9`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
