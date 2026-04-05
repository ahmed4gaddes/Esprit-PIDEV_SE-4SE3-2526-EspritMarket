import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RateService } from './rate.service';
import { Rate } from '../models/rate';

describe('RateService', () => {
  let service: RateService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/rates';

  const rate: Rate = { star: 5, comment: 'great' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RateService]
    });
    service = TestBed.inject(RateService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('addRate should POST with ratedUserId and withCredentials', () => {
    service.addRate(12, rate).subscribe();
    const req = httpMock.expectOne((r) => r.url === base && r.params.get('ratedUserId') === '12');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ ...rate, id: 1 });
  });

  it('getRatesForUser should GET user path', () => {
    let out: Rate[] | undefined;
    service.getRatesForUser(3).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/user/3`);
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('getAverageRating should GET average', () => {
    let avg: number | undefined;
    service.getAverageRating(3).subscribe((a) => (avg = a));
    const req = httpMock.expectOne(`${base}/user/3/average`);
    req.flush(4.5);
    expect(avg).toBe(4.5);
  });

  it('getRatesByRater should GET rater path', () => {
    let out: Rate[] | undefined;
    service.getRatesByRater(8).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/rater/8`);
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('update should PUT withCredentials', () => {
    service.update(7, rate).subscribe();
    const req = httpMock.expectOne(`${base}/7`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(rate);
  });

  it('delete should DELETE withCredentials', () => {
    service.delete(7).subscribe();
    const req = httpMock.expectOne(`${base}/7`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(null);
  });
});
