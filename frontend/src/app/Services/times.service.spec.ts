import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TimesService } from './times.service';
import { Times } from '../models/times';

describe('TimesService', () => {
  let service: TimesService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/times';

  const sample: Times = { type: 'LIVE', startTime: '2025-01-01T10:00:00Z' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TimesService]
    });
    service = TestBed.inject(TimesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('addTimes should POST with liveSessionId param', () => {
    service.addTimes(5, sample).subscribe();
    const req = httpMock.expectOne((r) => r.url === base && r.params.get('liveSessionId') === '5');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(sample);
    req.flush({ ...sample, id: 1 });
  });

  it('getAll should GET collection', () => {
    let out: Times[] | undefined;
    service.getAll().subscribe((r) => (out = r));
    const req = httpMock.expectOne(base);
    expect(req.request.method).toBe('GET');
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('getByLiveSession should GET by path', () => {
    let out: Times[] | undefined;
    service.getByLiveSession(9).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/live-session/9`);
    req.flush([]);
    expect(out).toEqual([]);
  });

  it('getById should GET one', () => {
    let out: Times | undefined;
    service.getById(3).subscribe((r) => (out = r));
    const req = httpMock.expectOne(`${base}/3`);
    req.flush(sample);
    expect(out).toEqual(jasmine.objectContaining({ type: 'LIVE' }));
  });

  it('update should PUT', () => {
    service.update(2, sample).subscribe();
    const req = httpMock.expectOne(`${base}/2`);
    expect(req.request.method).toBe('PUT');
    req.flush(sample);
  });

  it('delete should DELETE', () => {
    service.delete(4).subscribe();
    const req = httpMock.expectOne(`${base}/4`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
