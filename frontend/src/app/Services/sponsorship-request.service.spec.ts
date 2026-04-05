import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SponsorshipRequestService } from './sponsorship-request.service';

describe('SponsorshipRequestService', () => {
  let service: SponsorshipRequestService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/sponsorship-requests';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SponsorshipRequestService]
    });
    service = TestBed.inject(SponsorshipRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('createOffer should POST withCredentials', () => {
    const payload = { offerTitle: 'O' };
    service.createOffer(payload as any).subscribe();
    const req = httpMock.expectOne(base);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ id: 1, ...payload });
  });

  it('getMyCompanyOffers should GET company/me', () => {
    let done = false;
    service.getMyCompanyOffers().subscribe(() => (done = true));
    const req = httpMock.expectOne(`${base}/company/me`);
    expect(req.request.withCredentials).toBeTrue();
    req.flush([]);
    expect(done).toBeTrue();
  });

  it('getSponsorInbox should GET sponsor/inbox', () => {
    let done = false;
    service.getSponsorInbox().subscribe(() => (done = true));
    const req = httpMock.expectOne(`${base}/sponsor/inbox`);
    expect(req.request.withCredentials).toBeTrue();
    req.flush([]);
    expect(done).toBeTrue();
  });

  it('getSponsorHistory should GET sponsor/history', () => {
    let done = false;
    service.getSponsorHistory().subscribe(() => (done = true));
    const req = httpMock.expectOne(`${base}/sponsor/history`);
    expect(req.request.withCredentials).toBeTrue();
    req.flush([]);
    expect(done).toBeTrue();
  });

  it('decide should PUT decision', () => {
    const body = { approved: true, note: 'ok' };
    service.decide(10, body).subscribe();
    const req = httpMock.expectOne(`${base}/10/decision`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(body);
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ id: 10 });
  });

  it('getApprovedForCustomers should GET public without credentials requirement', () => {
    let done = false;
    service.getApprovedForCustomers().subscribe(() => (done = true));
    const req = httpMock.expectOne(`${base}/public/approved`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
    expect(done).toBeTrue();
  });
});
